package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

public class UserServerData {
    @SerializedName("data")
    private User data;
    public User getData() {
        return data;
    }
    public void setData(User data) {
        this.data = data;
    }
}
