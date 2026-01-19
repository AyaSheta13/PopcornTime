package com.example.popcorntime.data.model

data class Category(
    val id: Int,
    val name: String,
    val movies: List<Movie>
)