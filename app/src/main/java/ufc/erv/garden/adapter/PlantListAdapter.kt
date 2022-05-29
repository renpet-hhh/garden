package ufc.erv.garden.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ufc.erv.garden.data.Plant
import ufc.erv.garden.databinding.PlantItemBinding

typealias onPlantItemClickListener = ((View, Plant) -> Unit)


class PlantListAdapter(private val onItemClick: onPlantItemClickListener?) :
    ListAdapter<Plant, PlantListAdapter.PlantItemHolder>(DiffCallback) {
    class PlantItemHolder(
        private val binding: PlantItemBinding,
        private val onItemClick: onPlantItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.plant = plant
            Log.d("PlantListAdapter", "bind: $plant")
            if (onItemClick != null) {
                binding.root.setOnClickListener {
                    onItemClick.invoke(it, plant)
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantItemHolder {
        return PlantItemHolder(
            PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: PlantItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.description == newItem.description
        }

    }

}