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
import ufc.erv.garden.model.FormModel
import ufc.erv.garden.model.TaskModel


class ProfileModel: ContextualViewModel() {
    private val vTAG = "ProfileModel"
    private val fallbackProfile = Profile("", "", "", "")

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<Unit>(0)
    val saveSuccess = _saveSuccess.asSharedFlow()

    private fun isCityValid(city: String): Boolean {
        return CitiesByState.isValidCity(city, formModel.getText("state"))
    }
    private fun isStateValid(state: String): Boolean {
        return CitiesByState.isValidState(state)
    }

    val formModel = FormModel(viewModelScope) {
        text("name") {
            load { getCachedProfileData().name }
            error(0, "Deve possuir no mínimo 4 caracteres")
            check { if (text.length >= 4) null else 0 }
        }
        text("email") {
            load { getCachedProfileData().email }
            error(0, "Não contém @")
            check { if ('@' in text) null else 0 }
        }
        text("city") {
            load { getCachedProfileData().city }
            error(0, "Não é cidade do Estado")
            check { if (isCityValid(text)) null else 0 }
        }
        text("state") {
            load { getCachedProfileData().state }
            error(0, "Não é um Estado aceito")
            check { if (isStateValid(text)) null else 0 }
        }
        submit {
            trySave()
        }
    }

    private val getProfileTaskModel = TaskModel {
        getProfileData()
    }
    private var _cachedProfile: Profile? = null
    private suspend fun getCachedProfileData(): Profile {
        _cachedProfile?.let { return it }
        getProfileTaskModel.launch()
        return _cachedProfile!!
    }
    private suspend fun getProfileData() {
        _cachedProfile = kotlin.runCatching {
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


    private fun clearError() {
        _error.value = ""
        formModel.clearError()
    }
    fun resetToPreviousProfile() {
        formModel.resetToPrevious()
    }

    private fun getCurrentProfile(): Profile {
        return Profile(
            formModel.getText("name"),
            formModel.getText("email"),
            formModel.getText("city"),
            formModel.getText("state"),
        )
    }

    /* Assume que os campos estão válidos */
    fun trySave() {
        clearError()
        viewModelScope.launch {
            val profile = getCurrentProfile()
            val succeeded = save(profile)
            if (!succeeded) return@launch
            formModel.saveIfValid()
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