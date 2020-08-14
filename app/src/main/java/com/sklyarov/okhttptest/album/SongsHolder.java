package com.sklyarov.okhttptest.album;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Song;

public class SongsHolder extends RecyclerView.ViewHolder {

    private TextView mTitles;
    private TextView mDuration;

    public SongsHolder(@NonNull View itemView) {
        super(itemView);

        mTitles = itemView.findViewById(R.id.tv_title);
        mDuration = itemView.findViewById(R.id.tv_duration);
    }

    public void bind(Song item){
        mTitles.setText(item.getName());
        mDuration.setText(item.getDuration());
    }
}
