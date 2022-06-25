package ufc.erv.garden.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ufc.erv.garden.data.PlantAd
import ufc.erv.garden.databinding.PlantAdItemBinding

typealias OnClick = ((View, PlantAd) -> Unit)


class PlantAdListAdapter(
        private val onImageClick: OnClick?,
        private val onTradeClick: OnClick?,
        private val onBuyClick: OnClick?,
) :
    ListAdapter<PlantAd, PlantAdListAdapter.PlantAdItemHolder>(DiffCallback) {
    class PlantAdItemHolder(
        private val binding: PlantAdItemBinding,
        private val onImageClick: OnClick?,
        private val onTradeClick: OnClick?,
        private val onBuyClick: OnClick?,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ad: PlantAd) {
            binding.ad = ad
            val views: List<View> = listOf(binding.adPlantImage, binding.adPlantTradeButton, binding.adPlantBuyButton)
            val listeners = listOf(onImageClick, onTradeClick, onBuyClick)
            views.zip(listeners).forEach { (view, listener) ->
                if (listener != null) {
                    view.setOnClickListener {
                        listener.invoke(it, ad)
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantAdItemHolder {
        return PlantAdItemHolder(
            PlantAdItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onImageClick, onTradeClick, onBuyClick
        )
    }

    override fun onBindViewHolder(holder: PlantAdItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlantAd>() {
        override fun areItemsTheSame(oldItem: PlantAd, newItem: PlantAd): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantAd, newItem: PlantAd): Boolean {
            return oldItem.plant == newItem.plant
                    && oldItem.owner == newItem.owner
        }

    }

}