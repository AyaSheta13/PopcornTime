package com.example.popcorntime.data.model

import com.example.popcorntime.R

data class CastMember(
    val id: Int,
    val name: String,
    val characterName: String? = null,
    val imageRes: Int = R.drawable.no_found_cast_image,
    val imageUrl: String? = null
)