package com.example.wallpaperapp.Database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(
    @PrimaryKey val id: Int,
    val tags: String,
    val largeImageURL: String,
    val likes: Int,
    val user: String
)
