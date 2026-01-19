package com.example.popcorntime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
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
    val genres: List<GenreResponse>?,
    val runtime: Int?,
    val budget: Long?,
    val revenue: Long?,
    val status: String?,
    val popularity: Double?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    val tagline: String?
)

