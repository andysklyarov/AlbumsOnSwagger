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
import com.sklyarov.okhttptest.App;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.album.DetailAlbumFragment;
import com.sklyarov.okhttptest.db.MusicDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mErrorView;

    private final AlbumsAdapter mAlbumsAdapter = new AlbumsAdapter(album -> {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, DetailAlbumFragment.newInstance(album))
                .addToBackStack(DetailAlbumFragment.class.getSimpleName()) //todo !!! addToBackStack
                .commit();
    });


    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.albums);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAlbumsAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(() -> {
            getAlbums();
        });
    }

    @SuppressLint("CheckResult")
    private void getAlbums() {

        ApiUtilities.getApiService()
                .getAlbums()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(albums -> {
                    getMusicDao().insertAlbums(albums);
                })
                .onErrorReturn(throwable -> {
                    if (ApiUtilities.NETWORK_EXCEPTIONS.contains(throwable.getClass())) {
                        return getMusicDao().getAlbums();
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

    private MusicDao getMusicDao() {
        App app = (App)getActivity().getApplication();
        return app.getDatabase().getMusicDao();
    }
}
