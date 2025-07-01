package com.toiletfinder.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toiletfinder.app.data.firebase.ToiletRepo
import com.toiletfinder.app.data.model.Toilet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _toilets = MutableStateFlow<List<Toilet>>(emptyList())
    val toilets: StateFlow<List<Toilet>> = _toilets

    // Make isLoading observable
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadToilets() {
        viewModelScope.launch {
            try {
                _isLoading.value = true // Update the state flow
                val loaded = ToiletRepo.getToilets()
                _toilets.value = loaded
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false // Update the state flow
            }
        }
    }
}