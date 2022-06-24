package ufc.erv.garden.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ufc.erv.garden.data.PlantRequest
import ufc.erv.garden.databinding.PlantRequestItemBinding

typealias onPlantRequestItemClickListener = ((View, PlantRequest) -> Unit)


class PlantRequestListAdapter(private val onItemClick: onPlantRequestItemClickListener?) :
    ListAdapter<PlantRequest, PlantRequestListAdapter.PlantRequestItemHolder>(DiffCallback) {
    class PlantRequestItemHolder(
        private val binding: PlantRequestItemBinding,
        private val onItemClick: onPlantRequestItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: PlantRequest) {
            binding.request = request
            if (onItemClick != null) {
                binding.root.setOnClickListener {
                    onItemClick.invoke(it, request)
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantRequestItemHolder {
        return PlantRequestItemHolder(
            PlantRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: PlantRequestItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlantRequest>() {
        override fun areItemsTheSame(oldItem: PlantRequest, newItem: PlantRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantRequest, newItem: PlantRequest): Boolean {
            return oldItem.plant == newItem.plant
                    && oldItem.owner == newItem.owner
                    && oldItem.requester == newItem.requester
        }

    }

}