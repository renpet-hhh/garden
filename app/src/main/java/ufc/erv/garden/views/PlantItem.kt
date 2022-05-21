package ufc.erv.garden.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import ufc.erv.garden.R
import ufc.erv.garden.data.Plant

class PlantItem(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private lateinit var plant: Plant
    constructor(context: Context, attrs: AttributeSet? = null, plant: Plant) : this(context, attrs) {
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL
        this.setBackgroundResource(R.drawable.item_border)
        this.setPlant(plant)
    }
    private fun setPlant(plant: Plant) {
        this.plant = plant
        val popularName = TextView(context)
        val sciName = TextView(context)
        val description = TextView(context)
        val localization = TextView(context)
        val textViews : List<TextView> = listOf(popularName, sciName, description, localization)
        val texts : List<String> = listOf(
            context.getString(R.string.plant_item_popname, plant.popularName),
            context.getString(R.string.plant_item_sciname, plant.scientificName),
            context.getString(R.string.plant_item_description, plant.description),
            context.getString(R.string.plant_item_localization, plant.localization)
        )
        for ((view, text) in textViews zip texts) {
            view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            view.setTextColor(Color.rgb(100, 100, 100))
            view.text = text
            this.addView(view)
        }
    }


}