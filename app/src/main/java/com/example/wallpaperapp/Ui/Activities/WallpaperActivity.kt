package com.example.wallpaperapp.Ui.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapp.Adapters.PixabayAdapter
import com.example.wallpaperapp.Api.ApiRepository
import com.example.wallpaperapp.Api.ApiViewModel
import com.example.wallpaperapp.Api.ApiViewModelFactory
import com.example.wallpaperapp.Api.Hit
import com.example.wallpaperapp.Api.Response
import com.example.wallpaperapp.Api.RetrofitHelper
import com.example.wallpaperapp.Interfaces.ApiInterface
import com.example.wallpaperapp.Utils.NetworkUtil
import com.example.wallpaperapp.databinding.ActivityWallpaperBinding

class WallpaperActivity : AppCompatActivity() {
    private val binding by lazy { ActivityWallpaperBinding.inflate(layoutInflater) }
    private lateinit var apiViewModel: ApiViewModel
    private val apiKey = "45389061-0c63200f03ebe848478b314a9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModel()
        observeViewModel()

        setUpIntent()
        setOnClickListener()
        setContentView(binding.root)
    }

    private fun setupViewModel() {
        val apiInterface = RetrofitHelper.getInstance().create(ApiInterface::class.java)
        val repository = ApiRepository(apiInterface, this)
        apiViewModel =
            ViewModelProvider(this, ApiViewModelFactory(repository))[ApiViewModel::class.java]
    }

    private fun observeViewModel() {
        apiViewModel.wallpaper.observe(this, Observer { wallpaperResponse ->
            if (wallpaperResponse != null) {
                when (wallpaperResponse) {
                    is Response.Loading -> showLoadingState(true)
                    is Response.Success -> {
                        setupRecyclerView(wallpaperResponse.data!!.hits)
                        showLoadingState(false)
                    }

                    is Response.Error -> showError()
                }
            }
        })
    }

    private fun setupRecyclerView(hits: List<Hit>) {
        binding.rvWallpaper.apply {
            layoutManager = GridLayoutManager(this@WallpaperActivity, 2)
            adapter = PixabayAdapter(this@WallpaperActivity, hits.toMutableList())
        }
    }

    private fun setOnClickListener() {
        binding.ivBackIconWallpaper.setOnClickListener {
            finish()
        }
    }

    private fun setUpIntent() {
        val query = intent.getStringExtra("Key")
        val title = intent.getStringExtra("title")
        binding.tvWallaperTitle.text = title
        query?.let {
            if(NetworkUtil.isNetworkAvailable(applicationContext)){
                fetchImages(it)
            }else{
                showError()
            }

        }
    }

    private fun fetchImages(query: String) {
        showLoadingState(true)
        apiViewModel.getWallpaper(apiKey, query, 100)
    }

    private fun showLoadingState(isLoading: Boolean) {
        binding.shimmerWallpaper.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ivBackIconWallpaper.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvWallaperTitle.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.rvWallpaper.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    private fun showError() {
        binding.shimmerWallpaper.visibility = View.GONE
        binding.rvWallpaper.visibility = View.GONE
        binding.tvWallaperTitle.visibility = View.VISIBLE
        binding.ivBackIconWallpaper.visibility = View.VISIBLE
        binding.tvError.visibility = View.VISIBLE
    }
}
