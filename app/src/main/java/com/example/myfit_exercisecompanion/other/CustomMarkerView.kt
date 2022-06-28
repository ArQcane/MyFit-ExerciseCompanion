package com.example.myfit_exercisecompanion.other

import android.content.Context
import android.view.LayoutInflater
import com.example.myfit_exercisecompanion.databinding.MarkerViewBinding
import com.example.myfit_exercisecompanion.models.RunSession
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runSessions: List<RunSession>,
    c: Context,
    layoutId: Int
) : MarkerView(c,layoutId) {

    private var _binding: MarkerViewBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = MarkerViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null){
            return
        }
        val currentRunSessionId = e.x.toInt()
        val runSession = runSessions[currentRunSessionId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = runSession.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${runSession.avgSpeedInKMH}km/h"
        binding.tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${runSession.distanceInMeters / 1000f}km"
        binding.tvDistance.text = distanceInKm

        binding.tvDuration.text = TrackingUtility.getFormattedStopWatchTime(runSession.timeInMilis)

        val caloriesBurned = "${runSession.caloriesBurnt}kcal"
        binding.tvCaloriesBurned.text = caloriesBurned

        val stepsTaken = "${runSession.stepsPerSession} steps"
        binding.tvStepsTravelled.text = stepsTaken
    }

}