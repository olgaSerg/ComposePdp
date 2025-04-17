package com.example.composepdp.state

sealed class UiState {

    data object Loading : UiState()

    data object Success : UiState()

    data object Error : UiState()
}