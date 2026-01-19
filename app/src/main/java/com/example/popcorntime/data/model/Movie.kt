package com.example.popcorntime.data.model

import com.example.popcorntime.R

data class Movie(
    val id: Int,
    val title: String,
    val rating: Double,
    val imageRes: Int = R.drawable.no_internet_image,
    val category: String = "",
    val imageUrl: String? = null,
    val backdropUrl: String? = null,
    val description: String? = null,
    val releaseDate: String? = null,
    val genreIds: List<Int> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val voteCount: Int = 0,
    val popularity: Double? = null,
    val runtime: Int? = null
) {
    val fullImageUrl: String?
        get() = imageUrl ?: if (imageUrl != null) imageUrl else null
    
    val categoryName: String
        get() = if (genres.isNotEmpty()) genres.first().name else category
}
