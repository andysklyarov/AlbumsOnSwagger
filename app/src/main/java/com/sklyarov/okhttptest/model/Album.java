package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {
    @SerializedName("data")
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("release_date")
        private String releaseDate;
        @SerializedName("songs")
        private List<Song> songs;

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

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public List<Song> getSongs() {
            return songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }
    }
}
