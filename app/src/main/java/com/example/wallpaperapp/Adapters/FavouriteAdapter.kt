package com.example.wallpaperapp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapp.Database.Model.WallpaperEntity
import com.example.wallpaperapp.Ui.Activities.FavouriteActivity
import com.example.wallpaperapp.databinding.SingleRowDesingBinding

class FavouriteAdapter(
    private val context: Context,
    private val wallpaperList: List<WallpaperEntity>
) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = SingleRowDesingBinding.inflate(LayoutInflater.from(context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(wallpaperList[position], position)
    }

    override fun getItemCount(): Int {
        return wallpaperList.size
    }

    inner class FavouriteViewHolder(private val binding: SingleRowDesingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wallpaperEntity: WallpaperEntity, position: Int) {
            val wallpaper = wallpaperList[position]
            Glide.with(context)
                .load(wallpaper.largeImageURL)
                .into(binding.ivWallpaper)

            binding.ivWallpaper.setOnClickListener {
                val uri = wallpaperEntity.largeImageURL
                val id = wallpaperEntity.id
                //pass intent to favourite activity
                val intent = Intent(context, FavouriteActivity::class.java)
                intent.putExtra("previewImage", uri)
                intent.putExtra("id", id)
                context.startActivity(intent)
            }

        }
    }
}
