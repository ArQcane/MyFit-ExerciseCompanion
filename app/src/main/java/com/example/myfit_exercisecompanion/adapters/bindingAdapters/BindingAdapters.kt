package com.example.myfit_exercisecompanion.adapters.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myfit_exercisecompanion.adapters.foodAdapters.FoodListAdapter
import com.example.myfit_exercisecompanion.adapters.foodAdapters.FoodSearchListAdapter

import com.example.myfit_exercisecompanion.R
import com.example.myfit_exercisecompanion.models.Food
import com.example.myfit_exercisecompanion.models.FoodItem
import com.example.myfit_exercisecompanion.ui.viewModels.FoodApiStatus

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.ic_baseline_fastfood_24)
            error(R.drawable.ic_baseline_fastfood_24)
//            error(R.drawable.ic_no_wifi)
        }
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Food>?) {
    val adapter = recyclerView.adapter as FoodSearchListAdapter
    adapter.submitList(data)
}

@BindingAdapter("foodListData")
fun bindFoodListRecyclerView(recyclerView: RecyclerView, data: List<FoodItem>?) {
    val adapter = recyclerView.adapter as FoodListAdapter
    adapter.submitList(data)
}

@BindingAdapter("progressBarStatus")
fun bindProgressBar(progressBar: ProgressBar, status: FoodApiStatus?) {
    when (status) {
        FoodApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        FoodApiStatus.ERROR -> {
            progressBar.visibility = View.INVISIBLE // Or View.GONE
        }
        FoodApiStatus.DONE -> {
            progressBar.visibility = View.INVISIBLE
        }
        else -> {
            progressBar.visibility = View.INVISIBLE
        }
    }
}