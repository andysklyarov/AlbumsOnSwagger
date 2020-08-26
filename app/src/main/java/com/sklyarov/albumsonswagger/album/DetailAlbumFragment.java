package com.sklyarov.albumsonswagger.album;

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

import com.sklyarov.albumsonswagger.ApiUtilities;
import com.sklyarov.albumsonswagger.AuthFragment;
import com.sklyarov.albumsonswagger.CardDecoration;
import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.comments.CommentsFragment;
import com.sklyarov.albumsonswagger.db.DbUtils;
import com.sklyarov.albumsonswagger.db.MusicDao;
import com.sklyarov.albumsonswagger.model.Album;
import com.sklyarov.albumsonswagger.model.AlbumSong;
import com.sklyarov.albumsonswagger.model.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailAlbumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ALBUM_KEY = "ALBUM_KEY";

    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View errorView;
    private TextView errorViewText;
    private Album album;
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
                    .replace(R.id.fragmentContainer, CommentsFragment.newInstance(album, currentUser))
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
        recycler = view.findViewById(R.id.recycler);
        swipeRefreshLayout = view.findViewById(R.id.refresher);
        swipeRefreshLayout.setOnRefreshListener(this);
        errorView = view.findViewById(R.id.error_view);
        errorViewText = errorView.findViewById(R.id.tv_error_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            album = (Album) args.getSerializable(ALBUM_KEY);
            getActivity().setTitle(album.getName());
            currentUser = args.getString(AuthFragment.CURRENT_USER_KEY);
        }

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(mSongAdapter);

        musicDao = DbUtils.getDatabase().getMusicDao();

        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(() -> getAlbum());
    }

    @SuppressLint("CheckResult")
    private void getAlbum() {

        ApiUtilities.getApiService()
                .getAlbum(album.getId())
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
                        int albumId = album.getId();

                        List<Song> result = musicDao.getSongsFromAlbum(albumId);

                        album.setSongs(result);
                        return album;
                    } else {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> swipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> swipeRefreshLayout.setRefreshing(false))
                .subscribe(album -> {

                            if (album.getSongs().size() == 0) {
                                recycler.setVisibility(View.GONE);
                                errorViewText.setText("Песни еще не загружены. Включите интернет и попробуйте снова.");
                                errorView.setVisibility(View.VISIBLE);
                            }
                            else {
                                recycler.setVisibility(View.VISIBLE);
                                errorView.setVisibility(View.GONE);
                                mSongAdapter.addData(album.getSongs(), true);
                            }
                        }
                        , throwable -> {
                            recycler.setVisibility(View.GONE);
                            errorViewText.setText(R.string.error_text);
                            errorView.setVisibility(View.VISIBLE);
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
