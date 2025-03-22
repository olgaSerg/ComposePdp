package com.example.composepdp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.composepdp.state.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composepdp.utils.DishConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)
    val uiState: State<UiState> = _uiState

    private var items = DishConstants.DEFAULT_DISHES
    private val selectedItems = mutableStateListOf<String>()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = UiState.Loading
        items = DishConstants.DEFAULT_DISHES
        selectedItems.clear()

        viewModelScope.launch {
            delay(2000)
            val success = (0..1).random() == 1
            if (success) {
                _uiState.value = UiState.Success(items, selectedItems)
            } else {
                _uiState.value = UiState.Error
            }
        }
    }

    fun toggleSelection(item: String) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        updateState()
    }

    fun removeItem(item: String) {
        items = items.filterNot { it == item }
        selectedItems.remove(item)
        updateState()
    }

    private fun updateState() {
        _uiState.value = UiState.Success(items, selectedItems)
    }
}