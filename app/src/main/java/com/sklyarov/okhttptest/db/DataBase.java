package com.sklyarov.okhttptest.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.AlbumSong;
import com.sklyarov.okhttptest.model.Song;

@Database(entities = {Album.class, Song.class, AlbumSong.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    public abstract MusicDao getMusicDao();

}
