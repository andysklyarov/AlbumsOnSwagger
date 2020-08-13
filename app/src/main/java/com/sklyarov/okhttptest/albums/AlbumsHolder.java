package com.sklyarov.okhttptest.albums;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sklyarov.okhttptest.R;
import com.sklyarov.okhttptest.model.Albums;

public class AlbumsHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private TextView mRealiseDate;

    public AlbumsHolder(View itemView){
        super(itemView);

        mTitle = itemView.findViewById(R.id.tv_title);
        mRealiseDate = itemView.findViewById(R.id.tv_release_date);

    }

    public void bind(Albums.DataBean item){
        mTitle.setText(item.getName());
        mRealiseDate.setText(item.getReleaseDate());
    }
}
