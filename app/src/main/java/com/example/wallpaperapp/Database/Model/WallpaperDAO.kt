package com.example.wallpaperapp.Database.Model


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WallpaperDAO {

    @Insert
    suspend fun addWallpaper(wallpaperEntity: WallpaperEntity)

    @Delete
    suspend fun deleteWallpaper(wallpaperEntity: WallpaperEntity)

    @Query("SELECT * FROM wallpapers")
    fun getAllWallpaper() : LiveData<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE id = :id LIMIT 1")
    fun getWallpaperById(id: Int): LiveData<WallpaperEntity?>

}