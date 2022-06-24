package ufc.erv.garden.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.activity.viewModels
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


class MyPlantsActivity : DrawerBaseActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */
    private lateinit var binding: MyPlantsBinding


    private val viewModel: MyPlantsModel by viewModels()
    private val selectedPlantModel: SelectedPlantModel by viewModels()

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
        /* Sincroniza a configuração */
        if (key == "server") {
            viewModel.server = pref.getString("server", "mock") ?: "mock"
        }
    }

    private fun refreshPlants() {
        selectedPlantModel.deselect()
        viewModel.httpGetPlants()
    }

    private fun onPlantItemClick(plant: Plant) {
        Log.d(vTAG, "onPlantItemClick: $plant")
        selectedPlantModel.plant.value = plant
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        viewModel.server = defaultSharedPreferences.getString("server", "mock") ?: "mock"

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.my_plants,
            super.getRootForInflate(),
            true
        )
        binding.viewModel = viewModel
        binding.plantModel = selectedPlantModel
        binding.myPlantsList.adapter = PlantListAdapter { _, plant ->
            onPlantItemClick(plant)
        }
        binding.lifecycleOwner = this

        /* Listeners */
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(prefListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                refreshPlants()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}