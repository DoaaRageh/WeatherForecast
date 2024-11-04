package com.example.weatherforecast.favorite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.MainActivity
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavoriteBinding
import com.example.weatherforecast.databinding.FragmentSettingBinding
import com.example.weatherforecast.db.WeatherLocalDataSource
import com.example.weatherforecast.favorite.viewmodel.FavoriteViewModel
import com.example.weatherforecast.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherforecast.map.view.MapFragment
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.WeatherRemoteDataSource
import com.example.weatherforecast.setting.view.SettingFragment
import com.example.weatherforecast.weather.view.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), OnFavoriteClick {
    lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    lateinit var repository: WeatherRepository
    lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        repository = WeatherRepository.getInstance(
            WeatherRemoteDataSource.getInstance(), WeatherLocalDataSource.getInstance(requireContext()))

        favoritesAdapter = FavoritesAdapter(this)

        binding = FragmentFavoriteBinding.inflate(inflater, container, false )

        binding.btnAdd.setOnClickListener{
            (activity as? MainActivity)?.showFragment(MapFragment())
        }

        val factory = FavoriteViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = favoritesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collectLatest { favorites ->
                favoritesAdapter.submitList(favorites)
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
                FavoriteFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override suspend fun onRemoveClick(forecast: Forcast) {
        viewModel.updateFavoriteStatus(forecast)
    }

    override suspend fun onFacClick(forecast: Forcast) {
        TODO("Not yet implemented")
    }
}