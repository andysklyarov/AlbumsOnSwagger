package com.sklyarov.albumsonswagger.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.sklyarov.albumsonswagger.model.Album;
import com.sklyarov.albumsonswagger.model.AlbumSong;
import com.sklyarov.albumsonswagger.model.Comment;
import com.sklyarov.albumsonswagger.model.Song;

@Database(entities = {Album.class, Song.class, AlbumSong.class, Comment.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    public abstract MusicDao getMusicDao();

}
