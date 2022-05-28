package ufc.erv.garden

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.data.Plant
import ufc.erv.garden.databinding.ActivityMainBinding
import ufc.erv.garden.viewModel.MyPlantsModel
import ufc.erv.garden.views.PlantItem


class MainActivity : AppCompatActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */
    private lateinit var binding: ActivityMainBinding


    private val viewModel: MyPlantsModel by viewModels()

    private fun updateMyPlants(plants: List<Plant>) {
        binding.myPlantsList.removeAllViews()
        plants.forEach { binding.myPlantsList.addView(PlantItem(this.baseContext, null, it)) }
    }

    private fun tryRefresh(server: String) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        supportActionBar?.hide()

        /* Listeners */
        binding.serverInput.setEndIconOnClickListener {
            viewModel.server.value = binding.serverEdit.text.toString()
        }

        binding.serverEdit.addTextChangedListener {
            viewModel.clearError()
        }

        /* Collectors */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.server.collect {
                        tryRefresh(it)
                    }
                }
                launch {
                    viewModel.plants.collect {
                        updateMyPlants(it)
                    }
                }
                launch {
                    viewModel.error.collect {
                        Log.d(vTAG, "collect error")
                        /* Material3 não fornece o atributo de texto do erro
                        no XML, então temos que fazer isso manualmente */
                        binding.serverInput.error = it.ifEmpty { null }
                    }
                }
            }
        }

    }
}