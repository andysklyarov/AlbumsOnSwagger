package com.sklyarov.albumsonswagger.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Album.class, parentColumns = "id", childColumns = "album_id"),
        @ForeignKey(entity = Song.class, parentColumns = "id", childColumns = "song_id")})
public class AlbumSong {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "album_id")
    private int albumId;

    @ColumnInfo(name = "song_id")
    private int songId;

    @Ignore
    public AlbumSong() {
    }

    @Ignore
    public AlbumSong(int albumId, int songId) {
        this.albumId = albumId;
        this.songId = songId;
    }

    public AlbumSong(int id, int albumId, int songId) {
        this.id = id;
        this.albumId = albumId;
        this.songId = songId;
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

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
