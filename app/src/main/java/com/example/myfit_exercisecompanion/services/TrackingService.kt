package com.example.myfit_exercisecompanion.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.other.Constants.ACTION_PAUSE_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.ACTION_STOP_SERVICE
import com.example.myfit_exercisecompanion.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.myfit_exercisecompanion.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.myfit_exercisecompanion.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.myfit_exercisecompanion.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.myfit_exercisecompanion.other.Constants.NOTIFICATION_ID
import com.example.myfit_exercisecompanion.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.myfit_exercisecompanion.other.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.round

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>


@AndroidEntryPoint
class TrackingService : LifecycleService(), SensorEventListener {

    var isFirstRun = true

    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()


    @Inject
    lateinit var baseNotificationBuilder : NotificationCompat.Builder

    lateinit var currentNotificationBuilder : NotificationCompat.Builder

    private val sensorManager by lazy {
        this.getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private var initialSteps = -1

    companion object {
        val timeRunInMilis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val liveDistance = MutableLiveData<Float>()
        val liveCaloriesBurnt = MutableLiveData<Int>()
        val liveSteps = MutableLiveData<Int>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMilis.postValue(0L)
        liveDistance.postValue(0F)
        liveCaloriesBurnt.postValue(0)
        liveSteps.postValue(0)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        setupStepCounter()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    killService()
                    unloadStepCounter()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //Time diff between now and time started
                lapTime = System.currentTimeMillis() - timeStarted
                //post new lapTime
                timeRunInMilis.postValue(timeRun + lapTime)

                if(timeRunInMilis.value!! >= lastSecondTimeStamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_MUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_MUTABLE)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if(!serviceKilled){
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if(TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
    private val locationsList = mutableListOf<Location>()
    private var distance = 0f
    private var caloriesBurnt = 0



    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!) {
                result.locations.let { locations ->
                    for(location in locations) {
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                        locationsList.add(location)
                    }
                }
                calculateDistanceBetweenPathPoints(locationsList)
                getLiveCaloriesBurnt(liveDistance)
            }
        }
    }

    private fun calculateDistanceBetweenPathPoints(locationsList: MutableList<Location>){
        val lastLocation = locationsList.getOrNull(locationsList.lastIndex)
        val secondLastLocation = locationsList.getOrNull(locationsList.lastIndex - 1)
        val results = FloatArray(1)
        if(locationsList.size > 1){
            Location.distanceBetween(
                lastLocation!!.latitude,
                lastLocation.longitude,
                secondLastLocation!!.latitude ,
                secondLastLocation.longitude,
                results
            )
            distance += results[0]
            liveDistance.postValue(round(distance)/1000)
            Timber.d("New Live Distance = ${liveDistance.value}")
        } else{
            Timber.d("waiting for list to lengthen")
        }
    }
//    val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
    private var weight = 80f


    private fun getLiveCaloriesBurnt(liveDistance : MutableLiveData<Float>){
        if(liveDistance.value!! >= 0){
            caloriesBurnt = liveDistance.value?.times(weight)?.toInt()!!
            liveCaloriesBurnt.postValue(caloriesBurnt)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        //addEmptyPolyline()
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        setupStepCounter()

//        timeRunInSeconds.observe(this, Observer { timeRunInSeconds ->
//            liveDistance.observe(this, Observer { liveDistance ->
//                liveCaloriesBurnt.observe(this, Observer { liveCalories ->
//                    if(!serviceKilled){
//                        val notification = currentNotificationBuilder
//                            .setContentText(TrackingUtility.getFormattedStopwatchTime(timeRunInSeconds * 1000L) + " | " + TrackingUtility.getFormattedLiveDistance(liveDistance) + " | ${liveCalories}Kcal" )
//                        notificationManager.notify(NOTIFICATION_ID, notification.build())
//                    }
//                })
//            })
//        })
        timeRunInSeconds.observe(this, Observer {
            if (!serviceKilled) {
                val time = TrackingUtility.getFormattedStopWatchTime(it * 1000L)
                val info = "${liveDistance.value} m | " +
                        "${caloriesBurnt} kcal"
                baseNotificationBuilder
                    .setContentTitle(time)
                    .setContentText(info)
                notificationManager.notify(NOTIFICATION_ID, baseNotificationBuilder.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun setupStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(isTracking.value!!){
            event.values.firstOrNull()?.toInt()?.let { newSteps ->
                if (initialSteps == -1) {
                    initialSteps = newSteps
                }

                val currentSteps = newSteps - initialSteps

                liveSteps.postValue(currentSteps)
                Timber.d("currentSteps = ${currentSteps}")
            }
        }
    }

    fun unloadStepCounter() {
        if (stepCounterSensor != null) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}