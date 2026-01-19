package com.example.popcorntime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popcorntime.data.Result
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.repository.MovieRepository
import com.example.popcorntime.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SearchViewModel(
    private val repository: MovieRepository = MovieRepository(NetworkModule.tmdbApiService)
) : ViewModel() {
    
    private val _searchResults = MutableStateFlow<Result<List<Movie>>>(Result.Loading)
    val searchResults: StateFlow<Result<List<Movie>>> = _searchResults.asStateFlow()
    
    private val _filteredResults = MutableStateFlow<List<Movie>>(emptyList())
    val filteredResults: StateFlow<List<Movie>> = _filteredResults.asStateFlow()
    
    private var currentSearchQuery: String = ""
    private var currentResults: List<Movie> = emptyList()
    
    fun searchMovies(query: String, page: Int = 1) {
        if (query.isBlank()) {
            _searchResults.value = Result.Success(emptyList())
            _filteredResults.value = emptyList()
            currentResults = emptyList()
            return
        }
        
        viewModelScope.launch {
            _searchResults.value = Result.Loading
            currentSearchQuery = query
            
            // Debounce search
            delay(300)
            
            if (currentSearchQuery != query) return@launch
            
            when (val result = repository.searchMovies(query, page)) {
                is Result.Success -> {
                    val genres = repository.getGenres()
                    val genreList = (genres as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genreList)
                    currentResults = movies
                    _searchResults.value = Result.Success(movies)
                    _filteredResults.value = movies
                }
                is Result.Error -> {
                    _searchResults.value = result
                    _filteredResults.value = emptyList()
                }
                Result.Loading -> {}
            }
        }
    }
    
    fun filterResults(
        categories: Set<String> = emptySet(),
        minRating: Double = 0.0,
        year: String = "",
        minDuration: Int = 0,
        maxDuration: Int = 300
    ) {
        var filtered = currentResults
        
        if (categories.isNotEmpty()) {
            filtered = filtered.filter { movie ->
                movie.genres.any { genre -> categories.contains(genre.name) } ||
                categories.contains(movie.category)
            }
        }
        
        if (minRating > 0) {
            filtered = filtered.filter { it.rating >= minRating }
        }
        
        if (year.isNotBlank()) {
            filtered = filtered.filter { movie ->
                movie.releaseDate?.startsWith(year) == true
            }
        }
        
        if (minDuration > 0 || maxDuration < 300) {
            filtered = filtered.filter { movie ->
                val runtime = movie.runtime ?: 0
                runtime >= minDuration && runtime <= maxDuration
            }
        }
        
        _filteredResults.value = filtered
    }
    
    fun clearFilters() {
        _filteredResults.value = currentResults
    }
    
    fun discoverMovies(
        genreIds: List<Int>? = null,
        minRating: Double? = null,
        year: Int? = null,
        minRuntime: Int? = null,
        maxRuntime: Int? = null,
        page: Int = 1
    ) {
        viewModelScope.launch {
            _searchResults.value = Result.Loading
            when (val result = repository.discoverMovies(
                genreIds = genreIds,
                minRating = minRating,
                year = year,
                minRuntime = minRuntime,
                maxRuntime = maxRuntime,
                page = page
            )) {
                is Result.Success -> {
                    val genres = repository.getGenres()
                    val genreList = (genres as? Result.Success)?.data ?: emptyList()
                    val movies = repository.mapMoviesResponseToMovies(result.data, genreList)
                    currentResults = movies
                    _searchResults.value = Result.Success(movies)
                    _filteredResults.value = movies
                }
                is Result.Error -> {
                    _searchResults.value = result
                    _filteredResults.value = emptyList()
                }
                Result.Loading -> {}
            }
        }
    }
}

