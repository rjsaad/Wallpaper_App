package com.example.wallpaperapp.Api

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wallpaperapp.Interfaces.ApiInterface
import com.example.wallpaperapp.Utils.NetworkUtil

class ApiRepository(private val apiInterface: ApiInterface , private val context: Context) {

    private val wallpaperLiveData = MutableLiveData<Response<PixabayResponse>>()

     val weather : LiveData<Response<PixabayResponse>>
        get() = wallpaperLiveData


    suspend fun getWallpaper(apiKey :String , q : String , perPage:Int ){

        if (NetworkUtil.isNetworkAvailable(context)){
            try {
                val result = apiInterface.getImages(apiKey, q, perPage)
                if (result.isSuccessful && result.body()!=null){
                    wallpaperLiveData.postValue(Response.Success(result.body()))
                }
            }catch (e : Exception){
                wallpaperLiveData.postValue(Response.Error(e.message.toString()))
            }

        }

    }
}