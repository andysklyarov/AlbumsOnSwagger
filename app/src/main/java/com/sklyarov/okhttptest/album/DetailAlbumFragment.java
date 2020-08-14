package com.sklyarov.okhttptest.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sklyarov.okhttptest.ApiUtilities;
import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.albums.AlbumsFragment;
import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.Albums;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailAlbumFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ALBUM_KEY = "ALBUM_KEY";
    private RecyclerView mRecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mErrorView;
    private Albums.DataBean mAlbum;

    @NonNull
    private final SongsAdapter mSongAdapter = new SongsAdapter();

    public static DetailAlbumFragment newInstance(Albums.DataBean album) {
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mAlbum = (Albums.DataBean) args.getSerializable(ALBUM_KEY);
            getActivity().setTitle(mAlbum.getName());
        }

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mSongAdapter);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getAlbums();
        });
    }

    private void getAlbums() {

        ApiUtilities.getApiService().getAlbum(mAlbum.getId()).enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                if (response.isSuccessful()) {
                    mRecycler.setVisibility(View.VISIBLE);
                    mErrorView.setVisibility(View.GONE);
                    mSongAdapter.addData(response.body().getData().getSongs(), true);

                } else {
                    mRecycler.setVisibility(View.GONE);
                    mErrorView.setVisibility(View.VISIBLE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                mRecycler.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
