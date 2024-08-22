package com.example.wallpaperapp.Api

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApiViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    fun getWallpaper(apiKey: String, q: String, perPage: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            apiRepository.getWallpaper(apiKey, q, perPage)
        }

    }

    val wallpaper: LiveData<Response<PixabayResponse>>
        get() = apiRepository.weather
}