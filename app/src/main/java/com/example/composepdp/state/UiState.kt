package com.example.composepdp.state

import com.example.composepdp.viewmodel.model.DishItem

sealed class UiState {

    data object Loading : UiState()

    data class Success(val dishes: List<DishItem>) : UiState()

    data object Error : UiState()
}