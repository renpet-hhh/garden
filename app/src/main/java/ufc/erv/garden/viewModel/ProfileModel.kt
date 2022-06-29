package ufc.erv.garden.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ufc.erv.garden.data.CitiesByState
import ufc.erv.garden.data.Profile


class ProfileModel: ContextualViewModel() {
    private val vTAG = "ProfileModel"

    private val fallbackProfile = Profile("", "", "", "")
    private var savedProfile = fallbackProfile
    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val city = MutableStateFlow("")
    val state = MutableStateFlow("")


    fun fetchData() {
        viewModelScope.launch {
            resetToNewProfile(getProfileData())
        }
    }

    private suspend fun getProfileData(): Profile {
        return kotlin.runCatching {
            val response = client.get("${settings.server}/profile")
            Log.d(vTAG, response.bodyAsText(Charsets.UTF_8))
            if (response.status != HttpStatusCode.OK) return@runCatching fallbackProfile
            return@runCatching Json.decodeFromString<Profile>(response.bodyAsText(Charsets.UTF_8))
        }.getOrElse {
            fallbackProfile
        }
    }

    private object ERROR {
        const val DECODE_RESPONSE = "Falha ao receber dados do servidor"
        const val INVALID_FIELD = "Campos inválidos"
        const val SAVE_FAILED = "Erro ao salvar os dados de perfil"
    }


    private val _saveSuccess = MutableSharedFlow<Unit>(0)
    val saveSuccess = _saveSuccess.asSharedFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _fetching = MutableStateFlow(false)
    val fetching = _fetching.asStateFlow()

    private fun clearError() {
        _error.value = ""
    }
    fun resetToPreviousProfile() {
        clearError()
        val p = savedProfile.copy()
        name.value = p.name
        email.value = p.email
        city.value = p.city
        state.value = p.state
    }
    private fun resetToNewProfile(profile: Profile) {
        savedProfile = profile
        resetToPreviousProfile()
    }

    private fun getCurrentProfile(): Profile {
        return Profile(
            name.value, email.value, city.value, state.value
        )
    }
    private fun isValid(profile: Profile): Boolean {
        return CitiesByState.isValidCity(profile.city, profile.state)
    }
    fun trySave() {
        clearError()
        _fetching.value = true
        viewModelScope.launch {
            val profile = getCurrentProfile()
            if (!isValid(profile)) {
                _error.value = ERROR.INVALID_FIELD
                return@launch
            }
            val succeeded = save(profile)
            if (!succeeded) return@launch
            resetToNewProfile(profile)
        }.invokeOnCompletion {
            _fetching.value = false
        }
    }

    private suspend fun save(profile: Profile): Boolean {
        val result = runCatching {
            client.post("${settings.server}/profile/save") {
                setBody(profile)
            }
        }
        if (result.isFailure) {
            _error.value = ERROR.SAVE_FAILED
            return false
        }
        _saveSuccess.emit(Unit)
        return true
    }



}