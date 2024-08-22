package com.example.wallpaperapp.Interfaces

import com.example.wallpaperapp.Api.PixabayResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("api/")
    suspend fun getImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("per_page") perPage: Int ,
        @Query("image_type") imageType: String = "photo"
    ): Response<PixabayResponse>
}