package ufc.erv.garden.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ufc.erv.garden.bindImageUrl
import ufc.erv.garden.data.Plant
import ufc.erv.garden.data.readSettingsInfo
import ufc.erv.garden.databinding.PlantItemBinding
import ufc.erv.garden.singleton.Client

typealias onPlantItemClickListener = ((View, Plant) -> Unit)


class PlantListAdapter(
    private val lifeCycleOwner: LifecycleOwner,
    private val onItemClick: onPlantItemClickListener?
    ) :
    ListAdapter<Plant, PlantListAdapter.PlantItemHolder>(DiffCallback) {
    class PlantItemHolder(
        private val binding: PlantItemBinding,
        private val onItemClick: onPlantItemClickListener?,
        private val server: String,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            val username = Client.data().username
            binding.plant = plant
            val url = if (server == "mock") "mock" else "$server/u/$username/plant/image/${plant.id}"
            bindImageUrl(binding.plantItemImage, url)
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
        val server = readSettingsInfo(parent.context).server
        val binding = PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.lifecycleOwner = lifeCycleOwner
        return PlantItemHolder(
            binding,
            onItemClick,
            server
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