package com.example.wallpaperapp.Database.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wallpaperapp.Database.Model.WallpaperRepository

class WallpaperViewModelFactory(private val wallpaperRepository: WallpaperRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WallpaperViewModel(wallpaperRepository) as T
    }
}