package com.sklyarov.okhttptest.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Albums;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsHolder> {

    private final List<Albums.DataBean> mAlbums = new ArrayList();
    private OnItemClickListener mListener;

    public AlbumsAdapter(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    @Override
    public AlbumsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_album, parent, false);
        return new AlbumsHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumsHolder holder, int position) {
        Albums.DataBean album = mAlbums.get(position);
        holder.bind(album, mListener);
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public void addData(List<Albums.DataBean> data, boolean isRefreshed) {
        if (isRefreshed) {
            mAlbums.clear();
        }
        mAlbums.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Albums.DataBean data);
    }
}
