package com.sklyarov.albumsonswagger.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity
public class Comment implements Serializable {

    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("album_id")
    @ColumnInfo(name = "album_id")
    private int albumId;

    @SerializedName("text")
    @ColumnInfo(name = "text")
    private String text;

    @SerializedName("author")
    @ColumnInfo(name = "author")
    private String author;

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    public Comment(int id, int albumId, String text, String author, String timestamp) {
        this.id = id;
        this.albumId = albumId;
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    @Ignore
    public Comment(String text, int albumId) {
        id = 0;

        this.albumId = albumId;
        this.text = text;

        author = null;
        timestamp = null;
    }

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
