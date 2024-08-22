package com.example.wallpaperapp.Ui.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wallpaperapp.R
import com.example.wallpaperapp.Database.View.FavouriteFragment
import com.example.wallpaperapp.Ui.Fragments.CategoriesFragment
import com.example.wallpaperapp.Ui.Fragments.HomeFragment
import com.example.wallpaperapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        setOnClickListners()
    }

    private fun setOnClickListners() {
        binding.ivHome.setOnClickListener{
            replaceFragment(HomeFragment())
        }

        binding.ivFavourite.setOnClickListener{
            replaceFragment(FavouriteFragment())
        }
        binding.ivCategories.setOnClickListener{
            replaceFragment(CategoriesFragment())
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

}