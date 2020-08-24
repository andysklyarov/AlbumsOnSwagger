package com.sklyarov.okhttptest.album;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.sklyarov.okhttptest.AuthFragment;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.comments.CommentsFragment;
import com.sklyarov.okhttptest.db.DbUtils;
import com.sklyarov.okhttptest.db.MusicDao;
import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.AlbumSong;
import com.sklyarov.okhttptest.model.Song;

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
    private String currentUser;
    private MusicDao musicDao;

    @NonNull
    private final SongsAdapter mSongAdapter = new SongsAdapter();

    public static DetailAlbumFragment newInstance(Album album, String currentUser) {
        Bundle args = new Bundle();

        args.putSerializable(ALBUM_KEY, album);
        args.putString(AuthFragment.CURRENT_USER_KEY, currentUser);

        DetailAlbumFragment fragment = new DetailAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_albums, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_text) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, CommentsFragment.newInstance(mAlbum, currentUser))
                    .addToBackStack(CommentsFragment.class.getSimpleName())
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_recycler_albums_n_songs, container, false);
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
            currentUser = args.getString(AuthFragment.CURRENT_USER_KEY);
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mSongAdapter);

        musicDao = DbUtils.getDatabase().getMusicDao();

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

                    musicDao.insertSongs(songs);
                    musicDao.insertLinksAlbumSongs(newLinks);
                })
                .onErrorReturn(throwable -> {
                    if (ApiUtilities.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        int albumId = mAlbum.getId();

                        List<Song> result = musicDao.getSongsFromAlbum(albumId);

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

    private List<AlbumSong> createNewLinks(int albumId, List<Song> songs) { // todo avoid brute force
        List<AlbumSong> oldLinks = musicDao.getAlbumSongs();
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
}
