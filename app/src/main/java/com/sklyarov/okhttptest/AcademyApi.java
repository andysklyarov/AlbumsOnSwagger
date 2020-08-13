package com.sklyarov.okhttptest;

import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.Albums;
import com.sklyarov.okhttptest.model.Song;
import com.sklyarov.okhttptest.model.Songs;
import com.sklyarov.okhttptest.model.User;
import com.sklyarov.okhttptest.model.UserServerData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AcademyApi {

    @POST("registration")
    Call<Void> registration(@Body User user);

    @GET("user")
    Call<UserServerData> getUser(@Header("Authorization") String credentials);

    @GET("albums")
    Call<Albums> getAlbums();

    @GET("albums/{id}")
    Call<Album> getAlbums(@Path("id") int id);

    @GET("songs")
    Call<Songs> getSongs();

    @GET("songs/{id}")
    Call<Song> getSong(@Path("id") int id);
}