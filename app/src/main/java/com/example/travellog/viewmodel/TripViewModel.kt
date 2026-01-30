package com.example.travellog.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellog.data.AppDatabase
import com.example.travellog.model.Trip
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TripViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.tripDao()
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    // State
    val allTrips = dao.getAllTrips()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _currentSteps = MutableStateFlow(0)
    val currentSteps = _currentSteps.asStateFlow()

    private val _currentDistance = MutableStateFlow(0f)
    val currentDistance = _currentDistance.asStateFlow()

    private val _lastLocation = MutableStateFlow<Location?>(null)

    // Zmienne pomocnicze do sesji
    private var initialSteps = -1
    private var capturedPhotoUri: String? = null

    // --- SENSORY ---

    fun startTracking() {
        _isTracking.value = true
        _currentSteps.value = 0
        _currentDistance.value = 0f
        initialSteps = -1
        capturedPhotoUri = null
        _lastLocation.value = null

        // Rejestracja krokomierza
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Uruchomienie lokalizacji (uproszczone cykliczne odpytywanie co 5s w pętli dla prostoty projektu)
        startLocationUpdates()
    }

    fun stopTracking(title: String) {
        _isTracking.value = false
        sensorManager.unregisterListener(this)

        // Zapis do bazy
        val trip = Trip(
            title = title.ifBlank { "Wycieczka ${Date()}" },
            date = System.currentTimeMillis(),
            steps = _currentSteps.value,
            distance = _currentDistance.value,
            photoUri = capturedPhotoUri
        )
        viewModelScope.launch {
            dao.insertTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch { dao.deleteTrip(trip) }
    }

    fun setPhoto(uri: String) {
        capturedPhotoUri = uri
    }

    // Obsługa sensora kroków
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (initialSteps == -1) {
                initialSteps = totalSteps
            }
            _currentSteps.value = totalSteps - initialSteps
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Obsługa GPS
    @SuppressLint("MissingPermission") // Uprawnienia są sprawdzane w UI
    private fun startLocationUpdates() {
        viewModelScope.launch {
            while (_isTracking.value) {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        location?.let { newLoc ->
                            val lastLoc = _lastLocation.value
                            if (lastLoc != null) {
                                val dist = lastLoc.distanceTo(newLoc)
                                _currentDistance.value += dist
                            }
                            _lastLocation.value = newLoc
                        }
                    }
                kotlinx.coroutines.delay(5000) // update co 5 sekund
            }
        }
    }
}