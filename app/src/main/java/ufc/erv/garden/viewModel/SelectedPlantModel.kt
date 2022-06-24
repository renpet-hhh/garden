package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import ufc.erv.garden.data.Plant

class SelectedPlantModel: ViewModel() {
    val plant : MutableStateFlow<Plant?> = MutableStateFlow(null)
    fun deselect() {
        plant.value = null
    }
}
