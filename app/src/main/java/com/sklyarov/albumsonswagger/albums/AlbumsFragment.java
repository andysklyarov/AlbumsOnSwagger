package com.sklyarov.albumsonswagger.albums;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sklyarov.albumsonswagger.ApiUtilities;
import com.sklyarov.albumsonswagger.AuthFragment;
import com.sklyarov.albumsonswagger.CardDecoration;
import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.album.DetailAlbumFragment;
import com.sklyarov.albumsonswagger.db.DbUtils;
import com.sklyarov.albumsonswagger.db.MusicDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View errorView;
    private String currentUser;
    private MusicDao musicDao;

    private final AlbumsAdapter mAlbumsAdapter = new AlbumsAdapter(album -> {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, DetailAlbumFragment.newInstance(album, currentUser))
                .addToBackStack(DetailAlbumFragment.class.getSimpleName())
                .commit();
    });

    public static AlbumsFragment newInstance(String currentUser) {
        Bundle args = new Bundle();
        args.putString(AuthFragment.CURRENT_USER_KEY, currentUser);

        AlbumsFragment fragment = new AlbumsFragment();
        fragment.setArguments(args);
        return fragment;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.albums);

        Bundle args = getArguments();
        if (args != null) {
            currentUser = args.getString(AuthFragment.CURRENT_USER_KEY);
        } else {
            currentUser = null;
        }

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(mAlbumsAdapter);
        recycler.addItemDecoration(new CardDecoration());

        musicDao = DbUtils.getDatabase().getMusicDao();

        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(() -> getAlbums());
    }

    @SuppressLint("CheckResult")
    private void getAlbums() {

        ApiUtilities.getApiService()
                .getAlbums()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(albums -> {
                    musicDao.insertAlbums(albums);
                })
                .onErrorReturn(throwable -> {
                    if (ApiUtilities.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        return musicDao.getAlbums();
                    } else {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> swipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> swipeRefreshLayout.setRefreshing(false))
                .subscribe(albums -> {
                            recycler.setVisibility(View.VISIBLE);
                            errorView.setVisibility(View.GONE);
                            mAlbumsAdapter.addData(albums, true);
                        },
                        throwable -> {
                            recycler.setVisibility(View.GONE);
                            errorView.setVisibility(View.VISIBLE);
                        });
    }
}
