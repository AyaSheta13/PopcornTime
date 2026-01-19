package com.example.popcorntime.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

//@Entity(
//    tableName = "favorite_movies",
//    foreignKeys = [
//        ForeignKey(
//            entity = MovieEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["movieId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ]
//)

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey val movieId: Int
)


@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val coverImageRes: Int? = null,
    val userId: String? = null
)

@Entity(
    tableName = "playlist_movie_relation",
    primaryKeys = ["playlistId", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistMovieCrossRef(
    val playlistId: Int,
    val movieId: Int
)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val rating: Double,
    val imageRes: Int? = null,
    val category: String? = null,
    val imageUrl: String? = null,
    val backdropUrl: String? = null,
    val description: String? = null,
    val releaseDate: String? = null,
    val popularity: Double? = null,
    val voteCount: Int = 0,
    val runtime: Int? = null
)
