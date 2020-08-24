package com.sklyarov.okhttptest.albums;

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

import com.sklyarov.okhttptest.ApiUtilities;
import com.sklyarov.okhttptest.AuthFragment;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.album.DetailAlbumFragment;
import com.sklyarov.okhttptest.db.DbUtils;
import com.sklyarov.okhttptest.db.MusicDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mErrorView;
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
        mRecycler = view.findViewById(R.id.recycler);
        mSwipeRefreshLayout = view.findViewById(R.id.refresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mErrorView = view.findViewById(R.id.error_view);
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

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAlbumsAdapter);

        musicDao = DbUtils.getDatabase().getMusicDao();

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(() -> getAlbums());
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
                .doOnSubscribe(disposable -> mSwipeRefreshLayout.setRefreshing(true))
                .doFinally(() -> mSwipeRefreshLayout.setRefreshing(false))
                .subscribe(albums -> {
                            mRecycler.setVisibility(View.VISIBLE);
                            mErrorView.setVisibility(View.GONE);
                            mAlbumsAdapter.addData(albums, true);
                        },
                        throwable -> {
                            mRecycler.setVisibility(View.GONE);
                            mErrorView.setVisibility(View.VISIBLE);
                        });
    }
}
