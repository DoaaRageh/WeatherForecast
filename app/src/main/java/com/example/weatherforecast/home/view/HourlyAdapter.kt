package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.HourlyLayoutBinding
import com.example.weatherforecast.model.ForecastElement
import java.text.SimpleDateFormat
import java.util.Locale


class HourlyAdapter(val context: Context, var listener: (ForecastElement) -> Unit): ListAdapter<ForecastElement, HourlyAdapter.ViewHolder>(
    WeatherDiffUtil()
) {
    lateinit var binding: HourlyLayoutBinding
    val iconsUrl = "https://openweathermap.org/img/wn/"

    class ViewHolder(var binding: HourlyLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        binding = HourlyLayoutBinding.inflate(inflator, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourlyWeather = getItem(position)
        val imageUrl = iconsUrl + hourlyWeather.weather[0].icon + "@2x.png"
        Glide.with(context).load(imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(holder.binding.ivIcon)
        //holder.binding.tvTempreature.text = hourlyWeather.main.temp.toString()

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h a", Locale.getDefault())

        val date = inputFormat.parse(hourlyWeather.dt_txt)

        val formattedTime = outputFormat.format(date)
        holder.binding.tvTime.text = formattedTime

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val unit = prefs.getString("temperature", "k")
        val tempInKelvin= hourlyWeather.main.temp
        when (unit) {
            "c" -> {
                // Convert Kelvin to Celsius
                val tempInCelsius = tempInKelvin - 273.15
                holder.binding.tvTempreature.text = "%.1f".format(tempInCelsius.toDouble())
            }
            "f" -> {
                // Convert Kelvin to Fahrenheit
                val tempInFahrenheit = (tempInKelvin - 273.15) * 9/5 + 32
                holder.binding.tvTempreature.text = "%.1f".format(tempInFahrenheit.toDouble())
            }
            else -> {
                // Default to Kelvin
                holder.binding.tvTempreature.text = "%.1f".format(tempInKelvin.toDouble())
            }
        }

        //holder.binding.tvTime.text = SimpleDateFormat("h a", Locale.getDefault()).format(Date(hourlyWeather.dt * 1000))
        /*holder.binding.row.setOnClickListener {
            listener(product)
        }*/
    }

}