package com.example.weatherforecast.favorite.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.HourlyLayoutBinding
import com.example.weatherforecast.databinding.ItemFavoriteForecastBinding
import com.example.weatherforecast.model.Forcast
import com.example.weatherforecast.model.ForecastElement
import com.example.weatherforecast.weather.view.HourlyAdapter.ViewHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoritesAdapter(var listener: OnFavoriteClick) : ListAdapter<Forcast, FavoritesAdapter.ViewHolder>(FavoriteDiffCallback()) {
    lateinit var binding: ItemFavoriteForecastBinding

    class ViewHolder(var binding: ItemFavoriteForecastBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        binding = ItemFavoriteForecastBinding.inflate(inflator, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.binding.locationText.text = forecast.city
        holder.binding.btnRemove.setOnClickListener{
            GlobalScope.launch {
                listener.onRemoveClick(forecast)
            }
        }

        holder.binding.favoriteRow.setOnClickListener {
            GlobalScope.launch {
                listener.onFacClick(forecast)
            }
        }
    }

    class FavoriteDiffCallback : DiffUtil.ItemCallback<Forcast>() {
        override fun areItemsTheSame(oldItem: Forcast, newItem: Forcast): Boolean {
            return oldItem.city == newItem.city
        }

        override fun areContentsTheSame(oldItem: Forcast, newItem: Forcast): Boolean {
            return oldItem == newItem
        }
    }
}