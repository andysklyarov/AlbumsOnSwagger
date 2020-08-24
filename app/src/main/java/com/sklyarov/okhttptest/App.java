package com.sklyarov.okhttptest;

import android.app.Application;

import com.sklyarov.okhttptest.db.DbUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DbUtils.createDatabase(this);
    }
}