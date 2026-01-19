package com.example.popcorntime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popcorntime.data.Result
import com.example.popcorntime.data.model.Genre
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.repository.MovieRepository
import com.example.popcorntime.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val repository: MovieRepository = MovieRepository(NetworkModule.tmdbApiService)
) : ViewModel() {
    
    private val _featuredMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val featuredMovies: StateFlow<Result<List<Movie>>> = _featuredMovies.asStateFlow()
    
    private val _popularMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val popularMovies: StateFlow<Result<List<Movie>>> = _popularMovies.asStateFlow()
    
    private val _topRatedMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val topRatedMovies: StateFlow<Result<List<Movie>>> = _topRatedMovies.asStateFlow()
    
    private val _nowPlayingMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val nowPlayingMovies: StateFlow<Result<List<Movie>>> = _nowPlayingMovies.asStateFlow()
    
    private val _upcomingMovies = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val upcomingMovies: StateFlow<Result<List<Movie>>> = _upcomingMovies.asStateFlow()
    
    private val _genres = MutableStateFlow<Result<List<Genre>>>(Result.Loading)
    val genres: StateFlow<Result<List<Genre>>> = _genres.asStateFlow()
    
    private val _moviesByGenre = MutableStateFlow<Map<Int, Result<List<Movie>>>>(emptyMap())
    val moviesByGenre: StateFlow<Map<Int, Result<List<Movie>>>> = _moviesByGenre.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        loadGenres()
        loadFeaturedMovies()
        loadPopularMovies()
        loadTopRatedMovies()
        loadNowPlayingMovies()
        loadUpcomingMovies()
    }
    
    fun loadGenres() {
        viewModelScope.launch {
            _genres.value = Result.Loading
            _genres.value = repository.getGenres()
        }
    }
    
    fun loadFeaturedMovies() {
        viewModelScope.launch {
            _featuredMovies.value = Result.Loading
            when (val result = repository.getPopularMovies(1)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    _featuredMovies.value = Result.Success(movies.take(5))
                }
                is Result.Error -> _featuredMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            _popularMovies.value = Result.Loading
            when (val result = repository.getPopularMovies(page)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    _popularMovies.value = Result.Success(movies)
                }
                is Result.Error -> _popularMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadTopRatedMovies(page: Int = 1) {
        viewModelScope.launch {
            _topRatedMovies.value = Result.Loading
            when (val result = repository.getTopRatedMovies(page)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    _topRatedMovies.value = Result.Success(movies)
                }
                is Result.Error -> _topRatedMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadNowPlayingMovies(page: Int = 1) {
        viewModelScope.launch {
            _nowPlayingMovies.value = Result.Loading
            when (val result = repository.getNowPlayingMovies(page)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    _nowPlayingMovies.value = Result.Success(movies)
                }
                is Result.Error -> _nowPlayingMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadUpcomingMovies(page: Int = 1) {
        viewModelScope.launch {
            _upcomingMovies.value = Result.Loading
            when (val result = repository.getUpcomingMovies(page)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    _upcomingMovies.value = Result.Success(movies)
                }
                is Result.Error -> _upcomingMovies.value = result
                Result.Loading -> {}
            }
        }
    }
    
    fun loadMoviesByGenre(genreId: Int, page: Int = 1) {
        viewModelScope.launch {
            val currentMap = _moviesByGenre.value.toMutableMap()
            currentMap[genreId] = Result.Loading
            _moviesByGenre.value = currentMap
            
            when (val result = repository.discoverMovies(genreIds = listOf(genreId), page = page)) {
                is Result.Success -> {
                    val genres = (_genres.value as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genres)
                    currentMap[genreId] = Result.Success(movies)
                    _moviesByGenre.value = currentMap
                }
                is Result.Error -> {
                    currentMap[genreId] = result
                    _moviesByGenre.value = currentMap
                }
                Result.Loading -> {}
            }
        }
    }
    
    fun refresh() {
        loadInitialData()
    }
    
    fun refreshOnLanguageChange() {
        // مسح الـ cache وإعادة تحميل البيانات عند تغيير اللغة
        viewModelScope.launch {
            _genres.value = Result.Loading
            _featuredMovies.value = Result.Loading
            _popularMovies.value = Result.Loading
            _topRatedMovies.value = Result.Loading
            _nowPlayingMovies.value = Result.Loading
            _upcomingMovies.value = Result.Loading
            _moviesByGenre.value = emptyMap()
        }
        loadInitialData()
    }
}

