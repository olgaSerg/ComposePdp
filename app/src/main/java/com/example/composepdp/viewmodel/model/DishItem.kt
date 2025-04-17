package com.example.composepdp.viewmodel.model

data class DishItem(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false
)