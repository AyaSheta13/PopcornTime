package com.example.popcorntime.data.repository

import com.example.popcorntime.data.Result
import com.example.popcorntime.data.model.Genre
import com.example.popcorntime.data.model.Movie
import com.example.popcorntime.data.remote.ApiConfig
import com.example.popcorntime.data.remote.TmdbApiService
import com.example.popcorntime.data.remote.dto.*
import com.example.popcorntime.language.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(
    private val apiService: TmdbApiService
) {
    
    private var cachedGenres: List<Genre>? = null
    private var cachedLanguage: String? = null
    
    private fun getLanguageCode(): String {
        return LanguageManager.getApiLanguageCode()
    }
    
    suspend fun getPopularMovies(page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getPopularMovies(page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load popular movies: ${e.message}")
            }
        }
    }
    
    suspend fun getTopRatedMovies(page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getTopRatedMovies(page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load top rated movies: ${e.message}")
            }
        }
    }
    
    suspend fun getNowPlayingMovies(page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getNowPlayingMovies(page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load now playing movies: ${e.message}")
            }
        }
    }
    
    suspend fun getUpcomingMovies(page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getUpcomingMovies(page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load upcoming movies: ${e.message}")
            }
        }
    }
    
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetailsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getMovieDetails(movieId, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load movie details: ${e.message}")
            }
        }
    }
    
    suspend fun getMovieVideos(movieId: Int): Result<VideosResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getMovieVideos(movieId, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load videos: ${e.message}")
            }
        }
    }
    
    suspend fun getMovieCredits(movieId: Int): Result<CreditsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMovieCredits(movieId)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load movie credits: ${e.message}")
            }
        }
    }
    
    suspend fun getSimilarMovies(movieId: Int, page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.getSimilarMovies(movieId, page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load similar movies: ${e.message}")
            }
        }
    }
    
    suspend fun searchMovies(query: String, page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val response = apiService.searchMovies(query, page, language)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to search movies: ${e.message}")
            }
        }
    }
    
    suspend fun getGenres(): Result<List<Genre>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentLanguage = getLanguageCode()
                // إعادة تحميل الـ genres إذا تغيرت اللغة
                if (cachedGenres != null && cachedLanguage == currentLanguage) {
                    return@withContext Result.Success(cachedGenres!!)
                }
                val language = getLanguageCode()
                val response = apiService.getGenres(language)
                val genres = response.genres.map { genreResponse ->
                    Genre(
                        id = genreResponse.id,
                        name = genreResponse.name
                    )
                }
                cachedGenres = genres
                cachedLanguage = currentLanguage
                Result.Success(genres)
            } catch (e: Exception) {
                Result.Error(e, "Failed to load genres: ${e.message}")
            }
        }
    }
    
    suspend fun discoverMovies(
        genreIds: List<Int>? = null,
        sortBy: String = "popularity.desc",
        page: Int = 1,
        minRating: Double? = null,
        year: Int? = null,
        minRuntime: Int? = null,
        maxRuntime: Int? = null
    ): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val language = getLanguageCode()
                val genreIdsString = genreIds?.joinToString(",")
                val response = apiService.discoverMovies(
                    genreIds = genreIdsString,
                    sortBy = sortBy,
                    page = page,
                    language = language,
                    minRating = minRating,
                    year = year,
                    minRuntime = minRuntime,
                    maxRuntime = maxRuntime
                )
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(e, "Failed to discover movies: ${e.message}")
            }
        }
    }
    
    // Mapping functions
    fun mapMovieResponseToMovie(
        movieResponse: MovieResponse,
        genres: List<Genre> = emptyList()
    ): Movie {
        val movieGenres = movieResponse.genreIds?.mapNotNull { genreId ->
            genres.find { it.id == genreId }
        } ?: emptyList()
        
        val category = movieGenres.firstOrNull()?.name ?: ""
        
        return Movie(
            id = movieResponse.id,
            title = movieResponse.title,
            rating = movieResponse.voteAverage.coerceIn(0.0, 10.0), // Keep 10 scale
            category = category,
            imageUrl = movieResponse.posterPath?.let { "${ApiConfig.IMAGE_BASE_URL}$it" },
            backdropUrl = movieResponse.backdropPath?.let { "${ApiConfig.IMAGE_BASE_URL}$it" },
            description = movieResponse.description,
            releaseDate = movieResponse.releaseDate,
            genreIds = movieResponse.genreIds ?: emptyList(),
            genres = movieGenres,
            voteCount = movieResponse.voteCount,
            popularity = movieResponse.popularity
        )
    }
    
    fun mapMovieDetailsToMovie(
        movieDetails: MovieDetailsResponse,
        genres: List<Genre> = emptyList()
    ): Movie {
        val movieGenres = movieDetails.genres?.map { genreResponse ->
            Genre(genreResponse.id, genreResponse.name)
        } ?: emptyList()
        
        val category = movieGenres.firstOrNull()?.name ?: ""
        
        return Movie(
            id = movieDetails.id,
            title = movieDetails.title,
            rating = movieDetails.voteAverage.coerceIn(0.0, 10.0), // Keep 10 scale
            category = category,
            imageUrl = movieDetails.posterPath?.let { "${ApiConfig.IMAGE_BASE_URL}$it" },
            backdropUrl = movieDetails.backdropPath?.let { "${ApiConfig.IMAGE_BASE_URL}$it" },
            description = movieDetails.description,
            releaseDate = movieDetails.releaseDate,
            genreIds = movieGenres.map { it.id },
            genres = movieGenres,
            voteCount = movieDetails.voteCount,
            popularity = movieDetails.popularity,
            runtime = movieDetails.runtime
        )
    }
    
    fun mapMoviesResponseToMovies(
        moviesResponse: MoviesResponse,
        genres: List<Genre> = emptyList()
    ): List<Movie> {
        return moviesResponse.results.map { movieResponse ->
            mapMovieResponseToMovie(movieResponse, genres)
        }
    }
}

