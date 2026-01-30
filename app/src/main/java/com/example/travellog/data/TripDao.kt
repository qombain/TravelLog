package com.example.travellog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.travellog.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY date DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Insert
    suspend fun insertTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)
}