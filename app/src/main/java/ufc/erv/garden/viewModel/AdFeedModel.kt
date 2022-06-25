package ufc.erv.garden.viewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ufc.erv.garden.data.Plant
import ufc.erv.garden.data.PlantAd
import java.time.LocalDateTime

class AdFeedModel : ContextualViewModel() {
    private val _ads = MutableStateFlow<List<PlantAd>?>(null)
    /** Lista dos anúncios */
    val ads = _ads.asStateFlow()

    private val _selectedAd = MutableStateFlow<PlantAd?>(null)
    /** Anúncio associado à realização de proposta */
    val selectedAd = _selectedAd.asStateFlow()

    private val _tradePlant = MutableSharedFlow<Plant?>(1)
    /** Planta associada à realização de proposta de troca.
     * É a planta escolhida pelo usuário atual */
    val tradePlant = _tradePlant.asSharedFlow()

    fun reset() {
        _ads.value = null
    }
    fun refresh() {
        _ads.value = if (settings.server == "mock") _mockAds else httpRequest()
    }
    fun deselect() {
        _selectedAd.value = null
    }

    private fun httpRequest(): List<PlantAd> {
        return listOf()
    }
    fun selectAd(ad: PlantAd) {
        _selectedAd.value = ad
    }
    fun selectTradePlant(plant: Plant?) {
        viewModelScope.launch {
            _tradePlant.emit(plant)
        }
    }

    private val _date = LocalDateTime.of(2022, 2, 3, 8, 50)
    private val _mockAds = listOf(
        PlantAd("0", mockPlants[1], "mock-user10", _date),
        PlantAd("1", mockPlants[0], "mock-user5", _date),
        PlantAd("2", mockPlants[2], "mock-user7", _date),
        PlantAd("3", mockPlants[3], "mock-user8", _date),
        PlantAd("4", mockPlants[0], "mock-user10", _date),
        PlantAd("5", mockPlants[1], "mock-user10", _date),
        PlantAd("6", mockPlants[2], "mock-user10", _date),
        PlantAd("7", mockPlants[3], "mock-user10", _date),
        PlantAd("8", mockPlants[0], "mock-user10", _date),
        PlantAd("9", mockPlants[4], "mock-user3", _date),
        PlantAd("10", mockPlants[1], "mock-user88", _date),
        PlantAd("11", mockPlants[0], "mock-user99", _date),
        PlantAd("12", mockPlants[2], "mock-user76", _date),
        PlantAd("13", mockPlants[3], "mock-user78", _date),
        PlantAd("14", mockPlants[0], "mock-user1111", _date),
        PlantAd("15", mockPlants[1], "mock-user1", _date),
        PlantAd("16", mockPlants[4], "mock-user2", _date),
    )
}