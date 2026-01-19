package com.example.popcorntime.data.database

import androidx.room.*

@Dao
interface FavoriteDao {
    @Query("""
        SELECT m.* FROM movies m
        INNER JOIN favorite_movies f ON m.id = f.movieId
    """)
    suspend fun getAllFavorites(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorites(favorite: FavoriteMovie)

    @Query("DELETE FROM favorite_movies WHERE movieId = :movieId")
    suspend fun removeFromFavorites(movieId: Int)

    @Query("SELECT * FROM favorite_movies WHERE movieId = :movieId")
    suspend fun isFavorite(movieId: Int): FavoriteMovie?
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists WHERE userId = :userId")
    suspend fun getAllPlaylists(userId: String?): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :playlistId AND userId = :userId")
    suspend fun getPlaylistById(playlistId: Int, userId: String?): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMovieToPlaylist(crossRef: PlaylistMovieCrossRef)

    @Query("DELETE FROM playlist_movie_relation WHERE playlistId = :playlistId AND movieId = :movieId")
    suspend fun removeMovieFromPlaylist(playlistId: Int, movieId: Int)

    @Query("""
        SELECT m.* FROM movies m
        WHERE m.id IN (
            SELECT movieId FROM playlist_movie_relation WHERE playlistId = :playlistId
        )
    """)
    suspend fun getMoviesInPlaylist(playlistId: Int): List<MovieEntity>
    
    @Query("DELETE FROM playlists WHERE userId = :userId")
    suspend fun deleteAllPlaylistsForUser(userId: String?)
}

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)
}