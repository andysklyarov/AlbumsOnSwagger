package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentToReceive implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("text")
    private String text;
    @SerializedName("author")
    private String author;
    @SerializedName("timestamp")
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
