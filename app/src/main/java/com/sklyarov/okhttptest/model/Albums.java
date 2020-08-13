package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Albums {
    @SerializedName("data")
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("songs_count")
        private int songsCount;
        @SerializedName("release_date")
        private String releaseDate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSongsCount() {
            return songsCount;
        }

        public void setSongsCount(int songsCount) {
            this.songsCount = songsCount;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }
    }
}
