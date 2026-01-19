package com.example.popcorntime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreditsResponse(
    val cast: List<CastResponse>,
    val crew: List<CrewResponse>
)

data class CastResponse(
    val id: Int,
    val name: String,
    @SerializedName("character")
    val characterName: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("order")
    val order: Int
)

data class CrewResponse(
    val id: Int,
    val name: String,
    val job: String?,
    @SerializedName("profile_path")
    val profilePath: String?
)

