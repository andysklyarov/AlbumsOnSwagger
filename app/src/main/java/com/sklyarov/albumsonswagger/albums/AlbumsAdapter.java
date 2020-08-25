package com.sklyarov.albumsonswagger.albums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsHolder> {

    private final List<Album> mAlbums = new ArrayList();
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
        Album album = mAlbums.get(position);
        holder.bind(album, mListener);
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public void addData(List<Album> data, boolean isRefreshed) {
        if (isRefreshed) {
            mAlbums.clear();
        }

        data.sort((a, b) -> a.getId() - b.getId());

        mAlbums.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Album data);
    }
}