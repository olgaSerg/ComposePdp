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

    val items = mutableStateListOf<DishItem>()

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
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error
            }
        }
    }

    fun toggleSelection(item: DishItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item.copy(isSelected = !item.isSelected)
        }
    }

    fun removeItem(item: DishItem) {
        items.removeAll { it.id == item.id }
    }
}