package com.sklyarov.albumsonswagger.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Song {

        @SerializedName("id")
        @PrimaryKey
        @ColumnInfo(name = "id")
        private int id;

        @SerializedName("name")
        @ColumnInfo (name = "name")
        private String name;

        @SerializedName("duration")
        @ColumnInfo (name = "duration")
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
