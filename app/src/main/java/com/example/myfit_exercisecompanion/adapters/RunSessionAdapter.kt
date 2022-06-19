package com.example.myfit_exercisecompanion.adapters

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.databinding.ItemRunSessionBinding
import com.example.myfit_exercisecompanion.models.RunSession
import com.example.myfit_exercisecompanion.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunSessionAdapter : RecyclerView.Adapter<RunSessionAdapter.RunSessionViewHolder>(){

    inner class RunSessionViewHolder(val binding: ItemRunSessionBinding) : RecyclerView.ViewHolder(binding.root){
    }

    val diffCallback = object : DiffUtil.ItemCallback<RunSession>() {
        override fun areItemsTheSame(oldItem: RunSession, newItem: RunSession): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RunSession, newItem: RunSession): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<RunSession>) = differ.submitList(list)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunSessionViewHolder {
        val binding = ItemRunSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunSessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunSessionViewHolder, position: Int) {
        val runSession = differ.currentList[position]

        with(holder){
            holder.itemView.apply {
                Glide.with(this).load(runSession.img).into(binding.ivMapImage)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = runSession.timestamp
                }
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                binding.tvDate.text = "Posted on: ${dateFormat.format(calendar.time)}"

                val avgSpeed = "Pace: ${runSession.avgSpeedInKMH}km/h"
                binding.tvAverageSpeed.text = avgSpeed

                val distanceInKm = "Distance Travelled: ${runSession.distanceInMeters / 1000f}km"
                binding.tvDistance.text = distanceInKm

                binding.tvTimeTaken.text = "Time: ${TrackingUtility.getFormattedStopwatchTime(runSession.timeInMilis)}"

                val caloriesBurned = "Calories Burned: ${runSession.caloriesBurnt}kcal"
                binding.tvCaloriesBurned.text = caloriesBurned

                val stepsTaken = "${runSession.stepsPerSession} steps"
                binding.tvStepsTaken.text = stepsTaken

                val title = "${runSession.runSessionTitle}"
                binding.tvRunTitle.text = title
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}