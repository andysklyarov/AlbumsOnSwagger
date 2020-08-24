package com.sklyarov.okhttptest.db;

import android.app.Application;

import androidx.room.Room;

public class DbUtils {

    public static final String DATABASE_NAME = "music_database";
    private static DataBase database = null;

    public static void createDatabase(Application context) {
        context.deleteDatabase(DATABASE_NAME);

        database = Room.databaseBuilder(context, DataBase.class, "music_database")
                .allowMainThreadQueries()
                .build();
    }

    public static DataBase getDatabase() {
        return database;
    }
}
