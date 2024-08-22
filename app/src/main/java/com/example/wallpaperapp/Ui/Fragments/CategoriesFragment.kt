package com.example.wallpaperapp.Ui.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wallpaperapp.Ui.Activities.WallpaperActivity
import com.example.wallpaperapp.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        setOnClickListeners()
        return binding.root
    }

    private fun setOnClickListeners() {
        setupClickListener(binding.abstractLayout, "abstract", "Abstract")
        setupClickListener(binding.animalsLayout, "animals", "Animal")
        setupClickListener(binding.carsLayout, "cars", "Cars")
        setupClickListener(binding.citiesLayout, "city", "City")
        setupClickListener(binding.dogsLayout, "dogs", "Dogs")
        setupClickListener(binding.darkLayout, "dark", "Dark")
        setupClickListener(binding.fantasyLayout, "fantasy", "Fantasy")
        setupClickListener(binding.flowerLayout, "flower", "Flower")
        setupClickListener(binding.minimalistLayout, "minimalist", "Minimalist")
        setupClickListener(binding.musicLayout, "music", "Music")
        setupClickListener(binding.natureLayout, "nature", "Nature")
        setupClickListener(binding.neonLayout, "neon", "Neon")
        setupClickListener(binding.patternLayout, "pattern", "Pattern")
        setupClickListener(binding.plantsLayout, "plants", "Plants")
        setupClickListener(binding.quotesLayout, "quotes", "Quotes")
        setupClickListener(binding.quirkyLayout, "funny", "Quirky")
        setupClickListener(binding.spaceLayout, "space", "Space")
        setupClickListener(binding.sunsetLayout, "sunset", "Sunset")
        setupClickListener(binding.sportsLayout, "sports", "Sports")
        setupClickListener(binding.streetartLayout, "street art", "Street Art")
        setupClickListener(binding.technologyLayout, "technology", "Tech")
        setupClickListener(binding.textureLayout, "texture", "Texture")
        setupClickListener(binding.urbanLayout, "urban", "Urban")
        setupClickListener(binding.universeLayout, "universe", "Universe")
    }

    private fun setupClickListener(view: View, key: String, title: String) {
        view.setOnClickListener {
            val intent = Intent(requireContext(), WallpaperActivity::class.java).apply {
                putExtra("Key", key)
                putExtra("title", title)
            }
            startActivity(intent)
        }
    }
}
