package ufc.erv.garden

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.forEach
import ufc.erv.garden.data.Plant
import ufc.erv.garden.views.PlantItem



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myPlantsList = findViewById<LinearLayout>(R.id.myPlantsList)
        val mockPlants: List<Plant> = listOf(
            Plant(id="0", popularName="Beijo pintado", scientificName="Impatiens hawkeri",
                description="Regas frequentes (2 a 3 vezes por semana). Luminosidade: meia-sombra. Plantar em jardineiras ou canteiros", localization="Varandas"),
            Plant(id="1", popularName="Buxinho",
                description="Regar 1 vez por semana. Luminosidade: meia-sombra ou sol pleno. Plantar em canteiros"),
            Plant(id="2", popularName="Celósia",
                description="Regar de 1 a 2 vezes por semana. Luminosidade: sol pleno. Plantar em canteiros"),
            Plant(id="3", popularName="Comigo-ninguém-pode", scientificName="Dieffenbachia seguine",
                description="Regar de 1 a 2 vezes por semana. Luminosidade: meia-sombra ou sol pleno. Plantar em canteiros ou vasos"),
            Plant(id="4", popularName="Dália",
                description="Regar 2 vezes por semana. Luminosidade: meia-sombra. Plantar em vasos, jardineiras ou canteiros"),
        )
        mockPlants.forEach { myPlantsList.addView(PlantItem(this.baseContext, null, it)) }
    }
}