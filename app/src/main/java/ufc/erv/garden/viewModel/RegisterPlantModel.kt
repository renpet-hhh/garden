package ufc.erv.garden.viewModel

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufc.erv.garden.R
import ufc.erv.garden.data.Plant

class RegisterPlantModel : ViewModel() {
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
        // atualize o endereço do servidor antes de compilar!
        const val server = "https://f618-187-18-143-70.ngrok.io"
        const val register = "/register/plant"
    }

    lateinit var auth : SharedPreferences

    /* Descrição textual do erro */
    private val _error = MutableStateFlow("")
    val error by this::_error

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
        viewModelScope.launch {
            val plantBeginRegistered = Plant("0", popText.value, sciText.value, descText.value, localText.value)
            if (!validateInput()) return@launch
            val aUsername = auth.getString("username", "DEFAULT")
            val aPassword = auth.getString("password", "DEFAULT")
            if (aUsername == null || aPassword == null) {
                _error.emit(ERROR.FORGOT_AUTH)
                return@launch
            }
            val imgBytes = imageBytes.value
            if (imgBytes == null) {
                _error.emit(ERROR.CLIENT_ERROR)
                return@launch
            }
            val client = HttpClient(OkHttp) {
                install(Auth) {
                    digest {
                        credentials {
                            DigestAuthCredentials(aUsername, aPassword)
                        }
                    }
                }
            }
            val result = client.runCatching {
                submitFormWithBinaryData(
                    url = PATH.server + PATH.register,
                    formData = formData {
                        append("popularName", plantBeginRegistered.popularName)
                        append("scientificName", plantBeginRegistered.scientificName)
                        append("localization", plantBeginRegistered.localization)
                        append("description", plantBeginRegistered.description)
                        append("image", imgBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/${imageExt.value}")
                        })
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