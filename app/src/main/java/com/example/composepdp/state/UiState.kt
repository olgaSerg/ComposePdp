package com.example.composepdp.state

sealed class UiState {

    data object Loading : UiState()

    data class Success(val items: List<String>, val selectedItems: List<String>) : UiState()

    data object Error : UiState()
}