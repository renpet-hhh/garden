package ufc.erv.garden.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.data.Plant
import ufc.erv.garden.databinding.MyPlantsBinding
import ufc.erv.garden.viewModel.MyPlantsModel
import ufc.erv.garden.viewModel.SelectedPlantModel


class MyPlantsActivity : AppCompatActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */
    private lateinit var binding: MyPlantsBinding


    private val viewModel: MyPlantsModel by viewModels()
    private val selectedPlantModel: SelectedPlantModel by viewModels()

    private fun tryRefresh(server: String) {
        selectedPlantModel.plant.value = null // deselect
        if (server == "mock") {
            viewModel.setMockPlants()
            return
        }
        if (server == "") {
            viewModel.resetPlants()
            return
        }
        viewModel.httpGetPlants()
    }

    private fun onPlantItemClick(plant: Plant) {
        Log.d(vTAG, "onPlantItemClick: $plant")
        selectedPlantModel.plant.value = plant
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.my_plants)
        binding.viewModel = viewModel
        binding.plantModel = selectedPlantModel
        binding.myPlantsList.adapter = PlantListAdapter {
            _, plant -> onPlantItemClick(plant)
        }
        binding.lifecycleOwner = this

        supportActionBar?.hide()

        /* Listeners */
        binding.serverInput.setEndIconOnClickListener {
            viewModel.server.value = binding.serverEdit.text.toString()
        }

        /* Collectors */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.server.collect {
                        tryRefresh(it)
                    }
                }
            }
        }

    }
}