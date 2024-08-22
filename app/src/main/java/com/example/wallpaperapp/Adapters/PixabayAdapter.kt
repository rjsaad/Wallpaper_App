package com.example.wallpaperapp.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapp.Api.Hit
import com.example.wallpaperapp.Ui.Activities.PreviewActivity
import com.example.wallpaperapp.databinding.SingleRowDesingBinding

class PixabayAdapter(private val context: Context, private val wallpaperList: List<Hit>) :
    RecyclerView.Adapter<PixabayAdapter.PixabayViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PixabayAdapter.PixabayViewHolder {
        val binding = SingleRowDesingBinding.inflate(LayoutInflater.from(context), p0, false)
        return PixabayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PixabayViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return wallpaperList.size
    }

    inner class PixabayViewHolder(val binding: SingleRowDesingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            Log.d("imageLoad", "onResponse${wallpaperList[position].userImageURL}")
            Glide.with(context)
                .load(wallpaperList[position].largeImageURL)
                .into(binding.ivWallpaper)

            binding.ivWallpaper.setOnClickListener {
                val uri = wallpaperList[position].largeImageURL
                val id = wallpaperList[position].id
                val tags = wallpaperList[position].tags
                val likes = wallpaperList[position].likes
                val user = wallpaperList[position].user
                passIntent(uri , id , tags , likes , user)
            }
        }
    }

    private fun passIntent(uri: String , id : Int , tags : String , likes :Int , user : String) {
        val intent = Intent(context, PreviewActivity::class.java)
        intent.putExtra("previewImage", uri)
        intent.putExtra("id" , id )
        intent.putExtra("tags" , tags)
        intent.putExtra("likes" , likes)
        intent.putExtra("user" , user)
        context.startActivity(intent)
    }

}