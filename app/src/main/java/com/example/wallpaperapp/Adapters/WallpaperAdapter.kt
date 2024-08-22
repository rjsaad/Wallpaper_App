package com.example.wallpaperapp.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapp.ModelClasses.Wallpaper
import com.example.wallpaperapp.R
import com.example.wallpaperapp.Ui.Activities.PreviewActivity
import com.example.wallpaperapp.databinding.SingleRowDesingBinding
import com.google.firebase.storage.FirebaseStorage

class WallpaperAdapter(
    private val context: Context,
    private val wallpaperList: MutableList<Wallpaper>
) : RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val binding = SingleRowDesingBinding.inflate(LayoutInflater.from(context), parent, false)
        return WallpaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        holder.bind(wallpaperList[position])
    }

    override fun getItemCount(): Int = wallpaperList.size

    inner class WallpaperViewHolder(private val binding: SingleRowDesingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wallpaper: Wallpaper) {

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(wallpaper.link)

            // Get the download URL
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Log.d("WallpaperAdapter", "Download URL: $uri")

                // Load the image from the download URL using Glide
                Glide.with(context)
                    .load(uri)
                    .error(R.drawable.cities)
                    .into(binding.ivWallpaper)

                binding.ivWallpaper.setOnClickListener {
                    sendIntent(uri.toString()) // Passing the download URL
                }
            }.addOnFailureListener { exception ->
                // Handle errors
                Log.e("WallpaperAdapter", "Failed to get download URL", exception)
            }


        }
    }

    private fun sendIntent(uri: String) {
        val intent = Intent(context, PreviewActivity::class.java)
        intent.putExtra("previewImage", uri)
        Log.d("WallpaperAdapter", "Sending URL: $uri}") // Debug Log
        context.startActivity(intent)
    }

}
