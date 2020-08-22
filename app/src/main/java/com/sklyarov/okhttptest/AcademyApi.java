package com.sklyarov.okhttptest;

import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.CommentToReceive;
import com.sklyarov.okhttptest.model.CommentToSend;
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
    Completable sendComment(@Body CommentToSend comment);

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
    Single<List<CommentToReceive>> getComments(@Query("page") int page);



    @GET("albums/{id}/comments")
    Single<List<CommentToReceive>> getAlbumComments(@Path("id") int id);
}