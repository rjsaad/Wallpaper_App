package com.example.wallpaperapp.Database.Model

import androidx.lifecycle.LiveData

class WallpaperRepository(val wallpaperDAO: WallpaperDAO) {

    fun getAllWallpaper() :LiveData<List<WallpaperEntity>>{
        return wallpaperDAO.getAllWallpaper()
    }

    suspend fun addWallpaper(wallpaperEntity: WallpaperEntity){
        wallpaperDAO.addWallpaper(wallpaperEntity)
    }

    suspend fun deleteWallpaper(wallpaperEntity: WallpaperEntity){
        wallpaperDAO.deleteWallpaper(wallpaperEntity)
    }
    fun getWallpaperById(id: Int): LiveData<WallpaperEntity?> {
        return wallpaperDAO.getWallpaperById(id)
    }

}