package com.example.popcorntime.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.popcorntime.R
import com.example.popcorntime.data.model.Movie

@Composable
fun MovieImage(
    movie: Movie,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (!movie.imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            modifier = modifier,
            contentScale = contentScale,
            error = painterResource(id = movie.imageRes),
            placeholder = painterResource(id = movie.imageRes)
        )
    } else {
        Image(
            painter = painterResource(id = movie.imageRes),
            contentDescription = movie.title,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

