package com.sklyarov.okhttptest;

import android.app.Application;

import androidx.room.Room;

import com.sklyarov.okhttptest.db.DataBase;
import com.sklyarov.okhttptest.db.DbUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DbUtils.getDatabase(true, this);
    }
}