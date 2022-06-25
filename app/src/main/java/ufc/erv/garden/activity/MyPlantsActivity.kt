package ufc.erv.garden.activity

import android.app.Activity
import android.content.Intent
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ufc.erv.garden.R
import ufc.erv.garden.adapter.PlantListAdapter
import ufc.erv.garden.data.Plant
import ufc.erv.garden.databinding.MyPlantsBinding
import ufc.erv.garden.viewModel.MyPlantsModel
import ufc.erv.garden.viewModel.SelectedPlantModel


class MyPlantsActivity : DrawerBaseActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */
    private lateinit var binding: MyPlantsBinding

    object EXTRAS {
        /* Booleano. Indica se esta atividade deve exibir interface de selecionar planta
        * (com botão de confirmação) e retornar a planta selecionada. */
        const val RETURN_PLANT = "RETURN_PLANT"
        /* String representando Plant, a planta selecionada.
        Só é enviado se RETURN_PLANT é verdadeiro */
        const val RETURNED_PLANT = "RETURNED_PLANT"
    }


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
        viewModel.shouldReturnPlant = intent.getBooleanExtra(EXTRAS.RETURN_PLANT, false)
        binding = if (viewModel.shouldReturnPlant) {
            DataBindingUtil.setContentView(this, R.layout.my_plants)
        } else {
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.my_plants,
                super.getRootForInflate(),
                true
            )
        }
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

                if (viewModel.shouldReturnPlant) {
                    launch {
                        viewModel.returnedPlant.collect {
                            returnPlant(it)
                        }
                    }
                }
            }
        }

    }

    private fun returnPlant(plant: Plant?) {
        if (plant == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        val result = Intent().apply {
            putExtra(EXTRAS.RETURNED_PLANT, Json.encodeToString(plant))
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}