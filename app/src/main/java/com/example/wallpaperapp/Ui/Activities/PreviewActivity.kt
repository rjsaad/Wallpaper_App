package com.example.wallpaperapp.Ui.Activities

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
import com.example.wallpaperapp.Database.Model.WallpaperEntity
import com.example.wallpaperapp.Database.Model.WallpaperRepository
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModel
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModelFactory
import com.example.wallpaperapp.R
import com.example.wallpaperapp.databinding.ActivityPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPreviewBinding.inflate(layoutInflater) }
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
        val tags = intent.getStringExtra("tags")
        val likes = intent.getIntExtra("likes", 0)
        val user = intent.getStringExtra("user")
        val imageUrl = intent.getStringExtra("previewImage")

        imageUrl?.let { displayImage(it) }
        if (user != null && tags != null) {
            addToFavourites(id, tags, imageUrl ?: "", likes, user)
        }
    }

    private fun setupListeners() {
        binding.ivDownload.setOnClickListener { enablePermission() }
        binding.ivBackIcon.setOnClickListener { finish() }
        binding.ivShare.setOnClickListener { openDialog() }
    }

    private fun addToFavourites(id: Int, tags: String, imageUrl: String, likes: Int, user: String) {
        binding.ivFavourite.setOnClickListener {
            val liveData = wallpaperViewModel.getWallpaperById(id)

            liveData.observe(this) { existingWallpaper ->
                if (existingWallpaper != null) {
                    showToast("Wallpaper already in favorites")
                } else {
                    val wallpaperEntity = WallpaperEntity(id, tags, imageUrl, likes, user)
                    wallpaperViewModel.addWallpaper(wallpaperEntity)
                    showToast("Wallpaper Added")
                }
                // Remove all observers after the operation is done
                liveData.removeObservers(this)
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
        dialog.findViewById<TextView>(R.id.tv_setHome)
            ?.setOnClickListener { setWallpaper(WallpaperManager.FLAG_SYSTEM) }
        dialog.findViewById<TextView>(R.id.tv_setLock)
            ?.setOnClickListener { setWallpaper(WallpaperManager.FLAG_LOCK) }
        dialog.findViewById<TextView>(R.id.tv_setBoth)?.setOnClickListener {
            setWallpaperOnScreens()
        }
        dialog.findViewById<AppCompatButton>(R.id.btn_cancelDialog)
            ?.setOnClickListener { dialog.dismiss() }
    }

    private fun setWallpaper(flag: Int) {
        lifecycleScope.launch {
            if (imageUrl.isNullOrEmpty()) {
                showToast("Image URL is null or empty")
                return@launch
            }
            try {
                withContext(Dispatchers.IO) {
                    val bitmap = Glide.with(this@PreviewActivity)
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
                showToast("Failed to set wallpaper: ${e.message}")
            }
        }
    }

    private fun setWallpaperOnScreens() {
        lifecycleScope.launch {
            try {
                val bitmapForLock = withContext(Dispatchers.IO) {
                    Glide.with(this@PreviewActivity)
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
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                openAppSettings()
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun downloadImage(imageUrl: String?) {
        imageUrl?.let {
            val request = DownloadManager.Request(Uri.parse(it)).apply {
                setTitle("Downloading Wallpaper")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file-name.jpg")
            }
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            showToast("Downloading button clicked")
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
