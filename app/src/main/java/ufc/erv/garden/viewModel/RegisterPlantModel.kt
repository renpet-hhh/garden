package ufc.erv.garden.viewModel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.data.Plant

class RegisterPlantModel : ContextualViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private object ERROR {
        const val SERVER_CONNECT = "Conexão ao servidor falhou"
        const val INTERNAL_ERROR = "Erro interno do servidor"
        const val FORGOT_AUTH = "Cliente esqueceu os dados de autenticação"
        const val CLIENT_ERROR = "Erro interno do cliente"
        const val NOT_AUTHORIZED = "Nome de usuário e/ou senha incorreto"
        const val MISSING_POP_NAME = "Nome popular é campo obrigatório"
        const val MISSING_IMAGE = "Selecione uma foto"
    }
    private object PATH {
        const val register = "/register/plant"
    }

    /* Descrição textual do erro */
    private val _error = MutableStateFlow("")
    val error by this::_error
    
    private val _fetching = MutableStateFlow(false)
    val fetching by this::_fetching

    /* Indicador de sucesso da operação */
    private val _plant : MutableSharedFlow<Plant?> = MutableSharedFlow()
    val plant = _plant.asSharedFlow()

    /* Conteúdo dos campos de texto editáveis */
    val popText : MutableStateFlow<String> = MutableStateFlow("")
    val sciText : MutableStateFlow<String> = MutableStateFlow("")
    val localText : MutableStateFlow<String> = MutableStateFlow("")
    val descText : MutableStateFlow<String> = MutableStateFlow("")

    private val defaultURI = Uri.parse("android.resource://ufc.erv.garden/" + R.drawable.plant)
    private val _photoURI = MutableStateFlow(defaultURI)
    val photoURI = _photoURI.asStateFlow()

    val imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageExt = MutableStateFlow("")

    fun setPhotoURI(uri: Uri?) {
        _photoURI.value = uri ?: defaultURI
    }

    private suspend fun validateInput(): Boolean {
        val obligatoryFields = listOf(popText)
        obligatoryFields.forEach {
            it.value.ifEmpty {
                _error.emit(ERROR.MISSING_POP_NAME)
                return false
            }
        }
        if (photoURI.value == defaultURI) {
            _error.emit(ERROR.MISSING_IMAGE)
            return false
        }
        return true
    }

    fun tryRegisterPlant() {
        _fetching.value = true
        viewModelScope.launch {
            val plantBeginRegistered = Plant(0, popText.value, sciText.value, descText.value)
            if (!validateInput()) return@launch
            if (settings.server == "mock") {
                _plant.emit(plantBeginRegistered)
                return@launch
            }
            val imgBytes = imageBytes.value
            if (imgBytes == null) {
                _error.emit(ERROR.CLIENT_ERROR)
                return@launch
            }
            val result = client.runCatching {
                submitFormWithBinaryData(
                    url = settings.server + PATH.register,
                    formData = formData {
                        append("popularName", plantBeginRegistered.popularName)
                        append("scientificName", plantBeginRegistered.scientificName ?: "")
                        append("description", plantBeginRegistered.description ?: "")
                        appendInput("image", Headers.build {
                            append(HttpHeaders.ContentType, "image/${imageExt.value}")
                        }) {
                            buildPacket {
                                writeFully(imgBytes)
                            }
                        }
                    }
                )
            }
            if (result.isFailure) {
                _error.emit(ERROR.SERVER_CONNECT)
                return@launch
            }
            val response = result.getOrThrow()
            if (response.status != HttpStatusCode.Created) {
                _error.emit(ERROR.INTERNAL_ERROR)
                return@launch
            }
            clearError()
            _plant.emit(plantBeginRegistered)
            resetState()
        }.invokeOnCompletion {
            _fetching.value = false
        }
    }
    fun clearError() {
        _error.value = ""
    }
    private suspend fun resetState() {
        clearError()
        val texts = listOf(popText, sciText, localText, descText)
        texts.forEach { it.emit("") }
        _plant.emit(null)
        _photoURI.emit(defaultURI)
    }


}