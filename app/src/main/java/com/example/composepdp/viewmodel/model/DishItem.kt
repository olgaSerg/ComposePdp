package com.example.composepdp.viewmodel.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class DishItem(
    val id: Int,
    val name: String,
    val isSelected: MutableState<Boolean> = mutableStateOf(false)
)