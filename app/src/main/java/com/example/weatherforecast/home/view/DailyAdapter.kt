package com.example.weatherforecast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DailyLayoutBinding
import com.example.weatherforecast.model.DailyWeather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DailyAdapter(val context: Context, var listener: (DailyWeather) -> Unit): ListAdapter<DailyWeather, DailyAdapter.ViewHolder>(DailyDiffUtil()) {
    lateinit var binding: DailyLayoutBinding
    val iconsUrl = "https://openweathermap.org/img/wn/"

    class ViewHolder(var binding: DailyLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        binding = DailyLayoutBinding.inflate(inflator, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyWeather = getItem(position)
        val imageUrl = iconsUrl + dailyWeather.icon + "@2x.png"
        Glide.with(context).load(imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
            )
            .into(holder.binding.ivIcon)
        //holder.binding.tvDay.text = dailyWeather.date

        // Format the date to display the day name
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = dateFormat.parse(dailyWeather.date)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // EEEE for full day name (e.g., "Monday")
        val dayName = dayFormat.format(date ?: Date())

        // Set the day name and temperatures
        holder.binding.tvDay.text = dayName

        holder.binding.tvMaxTemp.text = dailyWeather.maxTemp.toString()
        holder.binding.tvMinTemp.text = dailyWeather.minTemp.toString()


        //holder.binding.tvTime.text = SimpleDateFormat("h a", Locale.getDefault()).format(Date(hourlyWeather.dt * 1000))
        /*holder.binding.row.setOnClickListener {
            listener(product)
        }*/
    }

}