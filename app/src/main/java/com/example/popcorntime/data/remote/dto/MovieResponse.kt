package com.example.popcorntime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: Int,
    val title: String,
    @SerializedName("overview")
    val description: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    val popularity: Double?,
    @SerializedName("adult")
    val isAdult: Boolean = false,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?
)

