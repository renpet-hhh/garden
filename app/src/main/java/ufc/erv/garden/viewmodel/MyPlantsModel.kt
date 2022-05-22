package ufc.erv.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ufc.erv.garden.data.Plant

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

class MyPlantsModel : ViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private val username = "mock-user"
    val server : MutableStateFlow<String> by lazy { MutableStateFlow<String>("") }
    private val _plants : MutableStateFlow<List<Plant>> by lazy { MutableStateFlow<List<Plant>>(listOf()) }
    val plants : StateFlow<List<Plant>> by this::_plants

    fun httpGetPlants() {
        viewModelScope.launch {
            val client = HttpClient(OkHttp) {
                developmentMode = true
            }
            val response = client.runCatching {
                get(server.value + "/u/${username}/plants")
            }
            if (response.isFailure) {
                server.value = ""
                return@launch
            }
            val body = response.getOrThrow().bodyAsText()
            if (body == "") return@launch
            val decoded = Json.runCatching { decodeFromString<List<Plant>>(body) }
            if (decoded.isFailure) {
                return@launch
            }
            _plants.value = decoded.getOrThrow()
        }
    }
    fun setMockPlants() {
        _plants.value = mockPlants
    }

}