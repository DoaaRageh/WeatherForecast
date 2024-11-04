package com.example.wezzo.model.POJOs

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val isNotification: Boolean
)
