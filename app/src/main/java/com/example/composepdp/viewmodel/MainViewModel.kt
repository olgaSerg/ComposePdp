package com.example.composepdp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.composepdp.state.UiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composepdp.utils.DishConstants
import com.example.composepdp.viewmodel.model.DishItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)
    val uiState: State<UiState> = _uiState

    private val items = mutableStateListOf<DishItem>()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            delay(2000)
            val success = (0..1).random() == 1
            if (success) {
                items.clear()
                items.addAll(DishConstants.DEFAULT_DISHES)
                updateUiState()
            } else {
                _uiState.value = UiState.Error
            }
        }
    }

    fun toggleSelection(item: DishItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val updatedItem = items[index].copy(isSelected = !items[index].isSelected)
            items[index] = updatedItem
            updateUiState()
        }
    }

    fun removeItem(item: DishItem) {
        val removed = items.remove(item)
        if (removed) {
            updateUiState()
        }
    }

    private fun updateUiState() {
        _uiState.value = UiState.Success(items)
    }
}