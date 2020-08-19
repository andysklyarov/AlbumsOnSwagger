package com.sklyarov.okhttptest;

import android.app.Application;

import androidx.room.Room;

import com.sklyarov.okhttptest.db.DataBase;

public class App extends Application {

    private DataBase database;

    @Override
    public void onCreate() {
        super.onCreate();

        deleteDatabase("music_database");

        database = Room.databaseBuilder(this, DataBase.class, "music_database")
                .allowMainThreadQueries()
                .build();
    }

    public DataBase getDatabase() {
        return database;
    }
}