package com.example.popcorntime.data.database

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import com.example.popcorntime.R
import com.example.popcorntime.data.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FavoritesManager {
    private val _favoriteMovies = mutableStateListOf<Movie>()
    val favoriteMovies: SnapshotStateList<Movie> = _favoriteMovies

    private var database: AppDatabase? = null

    fun initialize(context: Context) {
        database = AppDatabase.getInstance(context)
        loadFavorites()
    }

    private fun entityToMovie(entity: MovieEntity): Movie {
        return Movie(
            id = entity.id,
            title = entity.title,
            rating = entity.rating,
            imageRes = entity.imageRes ?: R.drawable.no_internet_image,
            category = entity.category ?: "",
            imageUrl = entity.imageUrl,
            backdropUrl = entity.backdropUrl,
            description = entity.description,
            releaseDate = entity.releaseDate,
            voteCount = entity.voteCount,
            popularity = entity.popularity,
            runtime = entity.runtime
        )
    }

    private fun movieToEntity(movie: Movie): MovieEntity {
        return MovieEntity(
            id = movie.id,
            title = movie.title,
            rating = movie.rating,
            imageRes = movie.imageRes,
            category = movie.category,
            imageUrl = movie.imageUrl,
            backdropUrl = movie.backdropUrl,
            description = movie.description,
            releaseDate = movie.releaseDate,
            popularity = movie.popularity,
            voteCount = movie.voteCount,
            runtime = movie.runtime
        )
    }

    private fun loadFavorites() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = database?.favoriteDao()?.getAllFavorites() ?: emptyList()
            _favoriteMovies.clear()
            _favoriteMovies.addAll(movies.map { entityToMovie(it) })
        }
    }

    fun addToFavorites(movie: Movie) {
        if (_favoriteMovies.any { it.id == movie.id }) return

        _favoriteMovies.add(movie)
        CoroutineScope(Dispatchers.IO).launch {
            // تأكد من وجود MovieEntity في جدول movies
            database?.movieDao()?.insertMovie(movieToEntity(movie))
            // ثم أدرج مرجع المفضلة
            database?.favoriteDao()?.addToFavorites(FavoriteMovie(movieId = movie.id))
        }
    }

    fun removeFromFavorites(movieId: Int) {
        _favoriteMovies.removeAll { it.id == movieId }
        CoroutineScope(Dispatchers.IO).launch {
            database?.favoriteDao()?.removeFromFavorites(movieId)
            // لا نحذف من جدول movies تلقائيًا؛ قد نحتاجه في المكتبة أو Playlists
        }
    }

    fun isFavorite(movieId: Int): Boolean {
        return _favoriteMovies.any { it.id == movieId }
    }

    fun toggleFavorite(movie: Movie) {
        if (isFavorite(movie.id)) removeFromFavorites(movie.id) else addToFavorites(movie)
    }

    // reload إذا احتجتي
    fun forceReload() {
        loadFavorites()
    }
}
