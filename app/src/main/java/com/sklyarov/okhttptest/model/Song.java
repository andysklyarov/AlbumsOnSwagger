package com.sklyarov.okhttptest.model;

import com.google.gson.annotations.SerializedName;

public class Song {

        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("duration")
        private String duration;

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

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
}
