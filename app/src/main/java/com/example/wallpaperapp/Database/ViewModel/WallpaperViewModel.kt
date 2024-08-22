package com.example.wallpaperapp.Database.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wallpaperapp.Database.Model.WallpaperEntity
import com.example.wallpaperapp.Database.Model.WallpaperRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WallpaperViewModel(private val wallpaperRepository: WallpaperRepository) : ViewModel(){

    fun getAllWallpaper() : LiveData<List<WallpaperEntity>>{
        return wallpaperRepository.getAllWallpaper()
    }

    fun addWallpaper(wallpaperEntity: WallpaperEntity){
        viewModelScope.launch(Dispatchers.IO){
            wallpaperRepository.addWallpaper(wallpaperEntity)
        }
    }

    fun deleteWallpaper(wallpaperEntity: WallpaperEntity){
        viewModelScope.launch (Dispatchers.IO){
            wallpaperRepository.deleteWallpaper(wallpaperEntity)
        }
    }
    fun getWallpaperById(id: Int): LiveData<WallpaperEntity?> {
        return wallpaperRepository.getWallpaperById(id)
    }
}