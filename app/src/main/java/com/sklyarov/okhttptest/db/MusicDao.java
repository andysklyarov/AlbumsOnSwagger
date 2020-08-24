package com.sklyarov.okhttptest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sklyarov.okhttptest.model.Album;
import com.sklyarov.okhttptest.model.AlbumSong;
import com.sklyarov.okhttptest.model.Comment;
import com.sklyarov.okhttptest.model.Song;

import java.util.List;

@Dao
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLinksAlbumSongs(List<AlbumSong> linksAlbumSongs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<Comment> comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOneComment(Comment comment);

    @Query("select * from album")
    List<Album> getAlbums();

    @Query("select * from comment")
    List<Comment> getComments();

    @Query("select * from comment where album_id = :albumId")
    List<Comment> getCommentsByAlbumId(int albumId);

    @Delete
    void deleteAlbum(Album album);

    @Query("DELETE FROM album where id = :albumId")
    void deleteAlbumById(int albumId);

    @Query("select * from Song")
    List<Song> getSongs();


    @Query("select * from song inner join albumsong on song.id = albumsong.song_id where album_id = :albumId order by song_id asc")
    List<Song> getSongsFromAlbum(int albumId);


    @Query("select * from albumsong")
    List<AlbumSong> getAlbumSongs();
}
