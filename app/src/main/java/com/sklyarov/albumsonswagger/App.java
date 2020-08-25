package com.sklyarov.albumsonswagger;

import android.app.Application;

import com.sklyarov.albumsonswagger.db.DbUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DbUtils.createDatabase(this);
    }
}