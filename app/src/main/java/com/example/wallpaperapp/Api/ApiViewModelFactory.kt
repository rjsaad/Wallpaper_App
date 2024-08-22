package com.example.wallpaperapp.Api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ApiViewModelFactory(private val apiRepository: ApiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ApiViewModel(apiRepository) as T
    }
}