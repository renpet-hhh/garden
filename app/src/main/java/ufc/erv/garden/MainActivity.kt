package ufc.erv.garden

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.data.Plant
import ufc.erv.garden.viewmodel.MyPlantsModel
import ufc.erv.garden.views.PlantItem






class MainActivity : AppCompatActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */

    private val model: MyPlantsModel by viewModels()
    private val myPlantsList by lazy { findViewById<LinearLayout>(R.id.myPlantsList) }

    private fun refreshMyPlants(plants: List<Plant>) {
        myPlantsList.removeAllViews()
        plants.forEach { myPlantsList.addView(PlantItem(this.baseContext, null, it)) }
    }

    private fun askServer() {
        Log.d(vTAG, "askServer")
        val input = EditText(this@MainActivity)
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Conectar ao servidor")
            .setMessage("Insira o endereço do servidor")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                model.server.value = input.text.toString()
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    model.server.collect {
                        if (it == "mock") {
                            model.setMockPlants()
                            return@collect
                        }
                        if (it == "") {
                            askServer()
                            return@collect
                        }
                        model.httpGetPlants()
                    }
                }
                launch {
                    model.plants.collect {
                        refreshMyPlants(it)
                    }
                }
            }
        }

    }
}