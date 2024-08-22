package com.example.wallpaperapp.Database.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WallpaperEntity::class] , version = 1)
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun wallpaperDao() : WallpaperDAO

    companion object{
        @Volatile
        private var INSTANCE : WallpaperDatabase ? = null

        fun getDatabase(context: Context) : WallpaperDatabase {
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context , WallpaperDatabase::class.java , "wallpaperDB").build()
                }
            }
            return INSTANCE!!
        }
    }
}