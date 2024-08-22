package com.example.wallpaperapp.Database.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapp.Adapters.FavouriteAdapter
import com.example.wallpaperapp.Database.Model.WallpaperDatabase
import com.example.wallpaperapp.Database.Model.WallpaperRepository
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModel
import com.example.wallpaperapp.Database.ViewModel.WallpaperViewModelFactory
import com.example.wallpaperapp.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var wallpaperViewModel: WallpaperViewModel
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val dao = WallpaperDatabase.getDatabase(requireContext()).wallpaperDao()
        val wallpaperRepository = WallpaperRepository(dao)
        wallpaperViewModel = ViewModelProvider(
            this,
            WallpaperViewModelFactory(wallpaperRepository)
        )[WallpaperViewModel::class.java]

        setupRecyclerView()
        displayFavWall()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvFavourites.layoutManager = GridLayoutManager(context, 2)
    }

    private fun displayFavWall() {
        wallpaperViewModel.getAllWallpaper().observe(viewLifecycleOwner, Observer { wallpaperList ->
            if (wallpaperList.isNotEmpty()) {
                binding.tvShowEmpty.visibility =View.GONE
                binding.rvFavourites.visibility =View.VISIBLE
                favouriteAdapter = FavouriteAdapter(requireContext(), wallpaperList)
                binding.rvFavourites.adapter = favouriteAdapter
            } else {
                binding.tvShowEmpty.visibility = View.VISIBLE
                binding.rvFavourites.visibility = View.GONE
            }
        })
    }
}
