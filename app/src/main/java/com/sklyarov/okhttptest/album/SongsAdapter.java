package com.sklyarov.okhttptest.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsHolder> {

    @NonNull
    private final List<Song> mSongs = new ArrayList<>();

    @NonNull
    @Override
    public SongsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_song, parent, false);
        return new SongsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsHolder holder, int position) {
        Song song = mSongs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public void addData(List<Song> data, boolean isRefreshed) {
        if (isRefreshed) {
            mSongs.clear();
        }

//        List<Song.DataBean> songs = new ArrayList<>();
//
//        for (int i = 0, size = data.size(); i < size; i++) {
//            songs.add(data.get(i).getData());
//        }

        mSongs.addAll(data);
        notifyDataSetChanged();
    }
}
