package com.sklyarov.albumsonswagger.album;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.albumsonswagger.R;
import com.sklyarov.albumsonswagger.model.Song;

public class SongsHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView duration;

    public SongsHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.tv_title);
        duration = itemView.findViewById(R.id.tv_duration);
    }

    public void bind(Song item) {
        title.setText(item.getName());
        duration.setText(item.getDuration());
    }
}
