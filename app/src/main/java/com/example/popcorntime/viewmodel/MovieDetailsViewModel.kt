package com.example.popcorntime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popcorntime.data.Result
import com.example.popcorntime.data.model.CastMember
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.remote.ApiConfig
import com.example.popcorntime.data.remote.dto.VideosResponse
import com.example.popcorntime.data.repository.MovieRepository
import com.example.popcorntime.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val repository: MovieRepository = MovieRepository(NetworkModule.tmdbApiService)
) : ViewModel() {
    
    private val _movieDetails = MutableStateFlow<Result<Movie>>(Result.Loading)
    val movieDetails: StateFlow<Result<Movie>> = _movieDetails.asStateFlow()
    
    private val _cast = MutableStateFlow<Result<List<CastMember>>>(Result.Loading)
    val cast: StateFlow<Result<List<CastMember>>> = _cast.asStateFlow()
    
    private val _similarMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val similarMovies: StateFlow<Result<List<Movie>>> = _similarMovies.asStateFlow()
    
    private val _videos = MutableStateFlow<Result<VideosResponse>>(Result.Loading)
    val videos: StateFlow<Result<VideosResponse>> = _videos.asStateFlow()
    
    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = Result.Loading
            when (val result = repository.getMovieDetails(movieId)) {
                is Result.Success -> {
                    val genres = repository.getGenres()
                    val genreList = (genres as? Result.Success)?.data ?: emptyList()
                    val movie = repository.mapMovieDetailsToMovie(result.data, genreList)
                    _movieDetails.value = Result.Success(movie)
                }
                is Result.Error -> _movieDetails.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadMovieCredits(movieId: Int) {
        viewModelScope.launch {
            _cast.value = Result.Loading
            when (val result = repository.getMovieCredits(movieId)) {
                is Result.Success -> {
                    val castMembers = result.data.cast.take(10).map { castResponse ->
                        CastMember(
                            id = castResponse.id,
                            name = castResponse.name,
                            characterName = castResponse.characterName,
                            imageUrl = castResponse.profilePath?.let { "${ApiConfig.IMAGE_BASE_URL}$it" }
                        )
                    }
                    _cast.value = Result.Success(castMembers)
                }
                is Result.Error -> _cast.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            _similarMovies.value = Result.Loading
            when (val result = repository.getSimilarMovies(movieId)) {
                is Result.Success -> {
                    val genres = repository.getGenres()
                    val genreList = (genres as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genreList)
                    _similarMovies.value = Result.Success(movies.take(10))
                }
                is Result.Error -> _similarMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadMovieVideos(movieId: Int) {
        viewModelScope.launch {
            _videos.value = Result.Loading
            _videos.value = repository.getMovieVideos(movieId)
        }
    }
    
    fun loadAllData(movieId: Int) {
        loadMovieDetails(movieId)
        loadMovieCredits(movieId)
        loadSimilarMovies(movieId)
        loadMovieVideos(movieId)
    }
}

