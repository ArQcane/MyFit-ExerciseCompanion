package com.example.myfit_exercisecompanion.adapters.foodAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfit_exercisecompanion.databinding.ItemFoodSearchBinding
import com.example.myfit_exercisecompanion.models.Food
import com.example.myfit_exercisecompanion.utility.CALORIE_ID
import com.example.myfit_exercisecompanion.utility.getNutrient

class FoodSearchListAdapter(private val onItemClicked: (Food) -> Unit) :
    ListAdapter<Food, FoodSearchListAdapter.FoodSearchViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodSearchViewHolder {
        return FoodSearchViewHolder(ItemFoodSearchBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FoodSearchViewHolder, position: Int) {
        val food = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(food)
        }
        holder.bind(food)
    }


    class FoodSearchViewHolder(private var binding: ItemFoodSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.food = food
            binding.executePendingBindings()

            // Add calorie textview
            binding.tvCalorie.text = getNutrient(food, CALORIE_ID)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Food>() {

        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.tag_id == newItem.tag_id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.tag_id == oldItem.tag_id // Dont really care
        }
    }
}