package com.example.wallpaperapp.Ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.wallpaperapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var apiViewModel: ApiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupViewModel()
        observeViewModel()
        if (NetworkUtil.isNetworkAvailable(requireContext())){
            fetchImages()
        }else{
            showError()
        }

        return binding.root
    }

    private fun setupViewModel() {
        val apiInterface = RetrofitHelper.getInstance().create(ApiInterface::class.java)
        val repository = ApiRepository(apiInterface, requireContext())
        apiViewModel =
            ViewModelProvider(this, ApiViewModelFactory(repository))[ApiViewModel::class.java]
    }

    private fun observeViewModel() {
        apiViewModel.wallpaper.observe(viewLifecycleOwner, Observer { wallpaperResponse ->
            if (isAdded) {
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

    private fun fetchImages() {
        showLoadingState(true)
        apiViewModel.getWallpaper(
            "45389061-0c63200f03ebe848478b314a9",
            "best of the month",
            100
        )
    }

    private fun setupRecyclerView(hits: List<Hit>) {
        binding.rvBestOfMonth.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = PixabayAdapter(requireContext(), hits)
        }
    }

    private fun showLoadingState(isLoading: Boolean) {
        if (isAdded) {
            binding.shimmerView.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvBestOfMonth.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvError.visibility = View.GONE
        }
    }

    private fun showError() {
        if (isAdded) {
            binding.shimmerView.visibility = View.GONE
            binding.rvBestOfMonth.visibility = View.GONE
            binding.titleText.visibility = View.GONE
            binding.tvError.visibility = View.VISIBLE
        }
    }
}
