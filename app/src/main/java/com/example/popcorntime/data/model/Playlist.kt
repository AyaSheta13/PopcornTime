package com.example.popcorntime.data.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String = "",
    val coverImageRes: Int? = null,
    val movies: MutableList<Movie> = mutableListOf()
) {
    val movieCount: Int
        get() = movies.size
}