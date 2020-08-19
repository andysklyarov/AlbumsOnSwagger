package com.sklyarov.okhttptest.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sklyarov.okhttptest.ApiUtilities;
import com.sklyarov.okhttptest.App;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.db.AlbumSong;
import com.sklyarov.okhttptest.db.MusicDao;
import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailAlbumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ALBUM_KEY = "ALBUM_KEY";
    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mErrorView;
    private TextView mErrorViewText;
    private Album mAlbum;

    @NonNull
    private final SongsAdapter mSongAdapter = new SongsAdapter();

    public static DetailAlbumFragment newInstance(Album album) {
        Bundle args = new Bundle();

        args.putSerializable(ALBUM_KEY, album);

        DetailAlbumFragment fragment = new DetailAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecycler = view.findViewById(R.id.recycler);
        mSwipeRefreshLayout = view.findViewById(R.id.refresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mErrorView = view.findViewById(R.id.error_view);
        mErrorViewText = mErrorView.findViewById(R.id.tv_error_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mAlbum = (Album) args.getSerializable(ALBUM_KEY);
            getActivity().setTitle(mAlbum.getName());
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mSongAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(() -> getAlbum());
    }

    @SuppressLint("CheckResult")
    private void getAlbum() {

        ApiUtilities.getApiService()
                .getAlbum(mAlbum.getId())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(album -> {
                    int albumId = album.getId();
                    List<Song> songs = album.getSongs();

                    List<AlbumSong> newLinks = createNewLinks(albumId, songs);

                    getMusicDao().insertSongs(songs);
                    getMusicDao().insertLinksAlbumSongs(newLinks);
                })
                .onErrorReturn(throwable -> {
                    if (ApiUtilities.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        int albumId = mAlbum.getId();

                        List<Song> result = getSongsByAlbumId(albumId);

                        mAlbum.setSongs(result);
                        return mAlbum;
                    } else {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mSwipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> mSwipeRefreshLayout.setRefreshing(false))
                .subscribe(album -> {

                            if (album.getSongs().size() == 0) {
                                mRecycler.setVisibility(View.GONE);
                                mErrorViewText.setText("Песни еще не загружены. Включите интернет и попробуйте снова.");
                                mErrorView.setVisibility(View.VISIBLE);
                            }
                            else {
                                mRecycler.setVisibility(View.VISIBLE);
                                mErrorView.setVisibility(View.GONE);
                                mSongAdapter.addData(album.getSongs(), true);
                            }
                        }
                        , throwable -> {
                            mRecycler.setVisibility(View.GONE);
                            mErrorViewText.setText(R.string.error_text);
                            mErrorView.setVisibility(View.VISIBLE);
                        });
    }

    @NotNull
    private List<Song> getSongsByAlbumId(int albumId) { // todo avoid brute force
        List<AlbumSong> links = getMusicDao().getAlbumSongs();
        List<Song> allSongs = getMusicDao().getSongs();
        List<Song> result = new ArrayList<>();
        for (AlbumSong link : links) {
            for (Song song : allSongs) {
                if (song.getId() == link.getSongId() && link.getAlbumId() == albumId) {
                    result.add(song);
                }
            }
        }
        return result;
    }

    private List<AlbumSong> createNewLinks(int albumId, List<Song> songs) { // todo avoid brute force
        List<AlbumSong> oldLinks = getMusicDao().getAlbumSongs();
        List<AlbumSong> newLinks = new ArrayList<>();
        for (Song song : songs) {
            boolean isLinkCreated = false;
            for (AlbumSong link : oldLinks) {
                if (link.getSongId() == song.getId() && link.getAlbumId() == albumId) {
                    isLinkCreated = true;
                    break;
                }
            }
            if (!isLinkCreated) {
                newLinks.add(new AlbumSong(albumId, song.getId()));
            }
        }
        return newLinks;
    }

    private MusicDao getMusicDao() {
        App app = null;
        Activity activity = getActivity();
        if (activity != null) {
            app = (App) activity.getApplication();
            return app.getDatabase().getMusicDao();
        }
        return null;
    }
}
