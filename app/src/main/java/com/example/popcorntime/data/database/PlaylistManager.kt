package com.example.popcorntime.data.database

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.popcorntime.R
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PlaylistManager {
    var playlists by mutableStateOf(emptyList<Playlist>())
    var showAddPlaylistDialog by mutableStateOf(false)
    var editingPlaylist: Playlist? by mutableStateOf(null)

    private var database: AppDatabase? = null
    private var nextPlaylistId = 1
    private var currentUserId: String? = null

    fun initialize(context: Context, userId: String? = null) {
        database = AppDatabase.getInstance(context)
        currentUserId = userId
        loadPlaylists()
    }
    
    fun setUserId(userId: String?) {
        currentUserId = userId
        loadPlaylists()
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

    private fun loadPlaylists() {
        CoroutineScope(Dispatchers.IO).launch {
            val playlistEntities = database?.playlistDao()?.getAllPlaylists(currentUserId) ?: emptyList()
            val loaded = playlistEntities.map { entity ->
                val movieEntities = database?.playlistDao()?.getMoviesInPlaylist(entity.id) ?: emptyList()
                val movies = movieEntities.map { entityToMovie(it) }.toMutableList()
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    coverImageRes = entity.coverImageRes ?: R.drawable.playlist_background_image,
                    movies = movies
                )
            }
            playlists = loaded
            nextPlaylistId = (loaded.maxOfOrNull { it.id } ?: 0) + 1
            // تهيئة صور قديمة لو احتجتي تعديل
            updateOldPlaylistsWithNewImage(playlistEntities)
        }
    }

    private fun updateOldPlaylistsWithNewImage(entities: List<PlaylistEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            entities.forEach { entity ->
                if (entity.coverImageRes == null || entity.coverImageRes == R.drawable.my_icon) {
                    database?.playlistDao()?.updatePlaylist(entity.copy(coverImageRes = R.drawable.playlist_background_image))
                }
            }
        }
    }

    fun addPlaylist(playlist: Playlist) {
        val newPlaylist = playlist.copy(
            id = nextPlaylistId,
            coverImageRes = R.drawable.playlist_background_image
        )
        playlists = playlists + newPlaylist
        showAddPlaylistDialog = false
        editingPlaylist = null
        nextPlaylistId++

        CoroutineScope(Dispatchers.IO).launch {
            database?.playlistDao()?.insertPlaylist(
                PlaylistEntity(
                    id = newPlaylist.id,
                    name = newPlaylist.name,
                    description = newPlaylist.description,
                    coverImageRes = newPlaylist.coverImageRes,
                    userId = currentUserId
                )
            )
        }
    }

    fun updatePlaylist(updatedPlaylist: Playlist) {
        val finalPlaylist = updatedPlaylist.copy(coverImageRes = R.drawable.playlist_background_image)
        playlists = playlists.map { if (it.id == finalPlaylist.id) finalPlaylist else it }
        showAddPlaylistDialog = false
        editingPlaylist = null

        CoroutineScope(Dispatchers.IO).launch {
            database?.playlistDao()?.updatePlaylist(
                PlaylistEntity(
                    id = finalPlaylist.id,
                    name = finalPlaylist.name,
                    description = finalPlaylist.description,
                    coverImageRes = finalPlaylist.coverImageRes,
                    userId = currentUserId
                )
            )
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        playlists = playlists.filter { it.id != playlist.id }
        CoroutineScope(Dispatchers.IO).launch {
            database?.playlistDao()?.deletePlaylist(
                PlaylistEntity(
                    id = playlist.id,
                    name = playlist.name,
                    description = playlist.description,
                    coverImageRes = playlist.coverImageRes,
                    userId = currentUserId
                )
            )
        }
    }

    fun editPlaylist(playlist: Playlist) {
        editingPlaylist = playlist
        showAddPlaylistDialog = true
    }

    fun addMovieToPlaylist(movie: Movie, playlistId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // التحقق من أن الفيلم غير موجود بالفعل في البلايليست
            val playlist = playlists.find { it.id == playlistId }
            if (playlist != null && playlist.movies.any { it.id == movie.id }) {
                // الفيلم موجود بالفعل في هذه البلايليست
                return@launch
            }
            
            // تأكد من وجود MovieEntity في جدول movies
            database?.movieDao()?.insertMovie(
                MovieEntity(
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
            )

            database?.playlistDao()?.addMovieToPlaylist(
                PlaylistMovieCrossRef(
                    playlistId = playlistId,
                    movieId = movie.id
                )
            )

            // تحديث القوائم المحلية - إضافة الفيلم فقط إذا لم يكن موجوداً
            val updatedPlaylists = playlists.map { playlist ->
                if (playlist.id == playlistId) {
                    val moviesList = playlist.movies.toMutableList()
                    if (!moviesList.any { it.id == movie.id }) {
                        moviesList.add(movie)
                    }
                    playlist.copy(movies = moviesList)
                } else {
                    playlist
                }
            }
            playlists = updatedPlaylists

            showSuccessMessage("${movie.title} added to playlist")
        }
    }

    fun removeMovieFromPlaylist(movieId: Int, playlistId: Int) {
        // تحديث الحالة مباشرة في الذاكرة
        val updatedPlaylists = playlists.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(movies = playlist.movies.filter { it.id != movieId }.toMutableList())
            } else {
                playlist
            }
        }
        playlists = updatedPlaylists

        CoroutineScope(Dispatchers.IO).launch {
            database?.playlistDao()?.removeMovieFromPlaylist(playlistId, movieId)

            // تحديث قاعدة البيانات فقط، بدون إعادة تحميل كامل
        }
    }

    // أدوات مساعدة للتصحيح
    fun debugPlaylistState() {
        println("PlaylistManager State:")
        println("showAddPlaylistDialog: $showAddPlaylistDialog")
        println("editingPlaylist: $editingPlaylist")
        println("playlists count: ${playlists.size}")
        println("database: $database")
    }

    fun showSuccessMessage(message: String) {
        println("Success: $message")
    }

    fun forceRefreshPlaylists() {
        playlists = emptyList()
        loadPlaylists()
    }

    fun clearAndReloadPlaylists() {
        playlists = emptyList()
        nextPlaylistId = 1
        loadPlaylists()
    }
    
    fun clearPlaylistsForUser(userId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.playlistDao()?.deleteAllPlaylistsForUser(userId)
        }
    }

    fun getPlaylistById(playlistId: Int): Playlist? {
        return playlists.firstOrNull { it.id == playlistId }
    }
}