package com.example.wallpaperapp.Ui.Activities

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wallpaperapp.Database.Model.WallpaperDatabase
import com.example.wallpaperapp.Database.Model.WallpaperRepository
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModel
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModelFactory
import com.example.wallpaperapp.R
import com.example.wallpaperapp.databinding.ActivityFavouriteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFavouriteBinding.inflate(layoutInflater) }
    private val REQUEST_CODE = 100
    private lateinit var wallpaperViewModel: WallpaperViewModel
    private var imageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        imageUrl = intent.getStringExtra("previewImage")
        setupViewModel()
        handleIntentExtras()
        setupListeners()

    }

    private fun setupViewModel() {
        val dao = WallpaperDatabase.getDatabase(applicationContext).wallpaperDao()
        val wallpaperRepository = WallpaperRepository(dao)
        wallpaperViewModel = ViewModelProvider(
            this,
            WallpaperViewModelFactory(wallpaperRepository)
        )[WallpaperViewModel::class.java]
    }

    private fun handleIntentExtras() {
        val id = intent.getIntExtra("id", 0)
        val imageUrl = intent.getStringExtra("previewImage")

        imageUrl?.let { displayImage(it) }

        deleteFromFav(id)
    }

    private fun setupListeners() {
        binding.ivDownload.setOnClickListener { enablePermission() }
        binding.ivBackIcon.setOnClickListener { finish() }
        binding.ivShare.setOnClickListener { openDialog() }
    }

    private fun deleteFromFav(id: Int) {
        binding.ivFavourite.setOnClickListener {
            wallpaperViewModel.getWallpaperById(id).observe(this) { existingWallpaper ->
                if (existingWallpaper != null) {
                    wallpaperViewModel.deleteWallpaper(existingWallpaper)
                    showToast("wallpaper removed from favourites")
                    finish()
                }
            }
        }
    }

    private fun openDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.set_wallpaper_botton_sheet, null)
        dialog.apply {
            setContentView(view)
            setCancelable(true)
            show()
        }
        setupDialogListeners(dialog)
    }

    private fun setupDialogListeners(dialog: BottomSheetDialog) {
        dialog.findViewById<TextView>(R.id.tv_setHome)?.setOnClickListener {
            setWallpaper(
                WallpaperManager.FLAG_SYSTEM
            )
        }
        dialog.findViewById<TextView>(R.id.tv_setLock)?.setOnClickListener {
            setWallpaper(
                WallpaperManager.FLAG_LOCK
            )
        }
        dialog.findViewById<TextView>(R.id.tv_setBoth)?.setOnClickListener {
            setWallpaperOnScreens()
        }
        dialog.findViewById<AppCompatButton>(R.id.btn_cancelDialog)
            ?.setOnClickListener { dialog.dismiss() }
    }

    private fun setWallpaperOnScreens() {
        lifecycleScope.launch {
            try {
                val bitmapForLock = withContext(Dispatchers.IO) {
                    Glide.with(this@FavouriteActivity)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get()
                }

                // Check if bitmap is successfully loaded
                if (bitmapForLock == null) {
                    showToast("Failed to load image")
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    WallpaperManager.getInstance(applicationContext)
                        .setBitmap(bitmapForLock, null, true, WallpaperManager.FLAG_LOCK)


                    WallpaperManager.getInstance(applicationContext)
                        .setBitmap(bitmapForLock, null, true, WallpaperManager.FLAG_SYSTEM)
                }
                showToast("Wallpaper set successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to set wallpaper: ${e.message}")
            }
        }
    }

    private fun setWallpaper(flag: Int) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val bitmap = Glide.with(this@FavouriteActivity)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get()
                    WallpaperManager.getInstance(applicationContext)
                        .setBitmap(bitmap, null, false, flag)
                }
                showToast("Wallpaper set successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to set wallpaper")
            }
        }
    }

    private fun enablePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadImage(imageUrl)
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            } else {
                downloadImage(imageUrl)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQUEST_CODE == requestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadImage(imageUrl)
        } else {
            showToast("Permission Denied")
        }
    }

    private fun downloadImage(imageUrl: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            imageUrl?.let {
                val request = DownloadManager.Request(Uri.parse(it)).apply {
                    setTitle("Downloading Wallpaper")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "file-name.jpg"
                    )
                }
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
                withContext(Dispatchers.Main) {
                    showToast("Downloading button clicked")
                }
            }
        }
    }

    private fun displayImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivPreview)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}