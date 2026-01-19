package com.example.popcorntime.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.model.Playlist

class MovieViewModel : ViewModel() {

    private val _favoriteMovies = mutableStateListOf<Movie>()
    val favoriteMovies: List<Movie> get() = _favoriteMovies

    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }

    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.contains(movie)
    }

    var playlists = mutableStateListOf<Playlist>()

    fun addPlaylist(playlist: Playlist) {
        playlists.add(playlist)
    }

    fun addMovieToPlaylist(movie: Movie, playlistId: Int) {
        playlists.find { it.id == playlistId }?.let { playlist ->
            playlist.movies.add(movie)
        }
    }

    fun addMovieToPlaylist2(movie: Movie, playlistId: Int) {
        val playlist = playlists.find { it.id == playlistId }
        if (playlist != null) {
            playlist.movies.add(movie)
        }
    }

    fun getPlaylistById(playlistId: Int): Playlist? {
        return playlists.find { it.id == playlistId }
    }

    fun removePlaylist(playlistId: Int) {
        playlists.removeAll { it.id == playlistId }
    }

    fun createPlaylist(name: String, description: String = ""): Playlist {
        val newId = playlists.maxOfOrNull { it.id }?.plus(1) ?: 1
        return Playlist(
            id = newId,
            name = name,
            description = description
        )
    }
}