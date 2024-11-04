package com.example.weatherforecast.alert.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.splitToIntList
import com.example.weatherforecast.R
import com.example.weatherforecast.model.AlarmRoom

class AlertAdapter(val listener:onAlertClickListener): ListAdapter<AlarmRoom, AlertAdapter.ViewHolder>(AlertDiffUtil()) {

    class ViewHolder(private val item:View): RecyclerView.ViewHolder(item) {
        var time:TextView=item.findViewById(R.id.tv_alarm_time)
        var remove: ImageButton =item.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alarm_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarms = getItem(position)
        Log.i("TAG", "onBindViewHolderAlert: ${alarms.formattedTime}")
        holder.time.text=alarms.formattedTime
        holder.remove.setOnClickListener {
            listener.onRemoveClickListner(alarms)
        }
    }


    }
