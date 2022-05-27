package ufc.erv.garden

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ufc.erv.garden.data.Plant
import ufc.erv.garden.databinding.ActivityMainBinding
import ufc.erv.garden.util.onTextFinished
import ufc.erv.garden.viewModel.MyPlantsModel
import ufc.erv.garden.views.PlantItem


class MainActivity : AppCompatActivity() {
    private val vTAG = "MainActivity" /* Logger TAG */
    private lateinit var binding: ActivityMainBinding


    private val viewModel: MyPlantsModel by viewModels()

    private fun refreshMyPlants(plants: List<Plant>) {
        binding.myPlantsList.removeAllViews()
        plants.forEach { binding.myPlantsList.addView(PlantItem(this.baseContext, null, it)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        supportActionBar?.hide()

        /* Listeners */
        binding.serverEdit.onTextFinished {
            viewModel.server.value = it
        }
        /* Collectors */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.server.collect {
                        if (it == "mock") {
                            viewModel.setMockPlants()
                            return@collect
                        }
                        if (it == "") {
                            viewModel.resetPlants()
                            return@collect
                        }
                        viewModel.httpGetPlants()
                    }
                }
                launch {
                    viewModel.plants.collect {
                        refreshMyPlants(it)
                    }
                }
            }
        }

    }
}