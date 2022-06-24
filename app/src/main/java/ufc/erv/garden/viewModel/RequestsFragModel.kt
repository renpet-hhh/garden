package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ufc.erv.garden.data.PlantRequest
import java.time.LocalDateTime

class RequestsFragModel: ViewModel() {


    lateinit var server: String
    lateinit var username: String
    private lateinit var cookie: String

    private val _requests = MutableStateFlow<List<PlantRequest>>(listOf())
    val requests = _requests.asStateFlow()

    private var _sent: Boolean = false

    fun initialize(server: String, username: String, cookie: String) {
        this.server = server
        this.username = username
        this.cookie = cookie
    }

    fun setType(sent: Boolean) {
        _sent = sent
    }

    fun refresh() {
        val shouldMock = server == "mock"
        if (_sent) {
            _requests.value = if (shouldMock) getMockSentRequests() else getSentRequests()
        } else {
            _requests.value = if (shouldMock) getMockReceivedRequests() else getReceivedRequests()
        }
    }

    private fun getMockSentRequests(): List<PlantRequest> {
        val date = LocalDateTime.of(2022, 1, 1, 6, 0, 0)
        return listOf(
            PlantRequest("0", mockPlants[0], username, "mock-user1", date),
            PlantRequest("1", mockPlants[1], username, "mock-user1", date),
            PlantRequest("2", mockPlants[2], username, "mock-user2", date),
            PlantRequest("3", mockPlants[3], username, "mock-user1", date),
        )
    }
    private fun getMockReceivedRequests(): List<PlantRequest> {
        val date = LocalDateTime.of(2022, 1, 2, 8, 30, 0)
        return listOf(
            PlantRequest("4", mockPlants[0], "mock-user3", username, date),
            PlantRequest("5", mockPlants[2], "mock-user4", username, date),
        )
    }

    private fun getSentRequests(): List<PlantRequest> {
        return listOf()
    }
    private fun getReceivedRequests(): List<PlantRequest> {
        return listOf()
    }

}