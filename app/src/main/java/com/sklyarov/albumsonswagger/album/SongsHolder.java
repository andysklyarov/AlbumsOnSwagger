package com.sklyarov.albumsonswagger.album;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.model.Song;

public class SongsHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private TextView mDuration;

    public SongsHolder(@NonNull View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tv_title);
        mDuration = itemView.findViewById(R.id.tv_duration);
    }

    public void bind(Song item) {
        mTitle.setText(item.getName());
        mDuration.setText(item.getDuration());
    }
}