package com.manoj.domain.entities

data class UiState(
    val showLoading: Boolean = true, val errorMessage: String? = null
)

data class MovieDetails(val movieId: Int)