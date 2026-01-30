package com.example.travellog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: Long,
    val steps: Int,
    val distance: Float, // w metrach
    val photoUri: String? = null
)