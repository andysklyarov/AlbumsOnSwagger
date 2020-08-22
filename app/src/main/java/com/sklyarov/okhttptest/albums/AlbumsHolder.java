package com.sklyarov.okhttptest.albums;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Album;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlbumsHolder extends RecyclerView.ViewHolder {

    private static final String INPUT_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss+00:00";
    private static final String OUTPUT_DATE_TIME_PATTERN = "dd MMM yyyy";

    private TextView mTitle;
    private TextView mRealiseDate;

    public AlbumsHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tv_title);
        mRealiseDate = itemView.findViewById(R.id.tv_release_date);
    }

    public void bind(Album item, AlbumsAdapter.OnItemClickListener onItemClickListener) {
        mTitle.setText(item.getName());

        String releaseDateString = item.getReleaseDate();
        LocalDateTime releaseDateTime = LocalDateTime.parse(releaseDateString, DateTimeFormatter.ofPattern(INPUT_DATE_TIME_PATTERN));
        String formattedDate = releaseDateTime.format(DateTimeFormatter.ofPattern(OUTPUT_DATE_TIME_PATTERN));

        mRealiseDate.setText(formattedDate);

        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(item));
        }
    }
}
