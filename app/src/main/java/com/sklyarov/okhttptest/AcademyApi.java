package com.sklyarov.okhttptest;

import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.Comment;
import com.sklyarov.okhttptest.model.Song;
import com.sklyarov.okhttptest.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AcademyApi {

    @POST("registration")
    Completable registration(@Body User user);

    @POST("comments")
    Completable sendComment(@Body Comment comment);

    @GET("user")
    Single<User> getUser();

    @GET("albums")
    Single<List<Album>> getAlbums();

    @GET("albums/{id}")
    Single<Album> getAlbum(@Path("id") int id);

    @GET("songs")
    Call<List<Song>> getSongs();

    @GET("songs/{id}")
    Call<Song> getSong(@Path("id") int id);

    @GET("comments")
    Single<List<Comment>> getComments(@Query("page") int page);

    @GET("albums/{id}/comments")
    Single<List<Comment>> getAlbumComments(@Path("id") int id);
}