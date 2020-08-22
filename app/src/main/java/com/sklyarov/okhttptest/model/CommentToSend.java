package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentToSend implements Serializable {

    @SerializedName("text")
    private String text;
    @SerializedName("album_id")
    private int albumId;

    public CommentToSend(String text, int albumId) {
        this.text = text;
        this.albumId = albumId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }


}
