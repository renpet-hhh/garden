package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufc.erv.garden.data.Plant

class RegisterPlantModel : ViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private object ERROR {
        const val SERVER_CONNECT = "Conexão ao servidor falhou"
        const val DECODE_RESPONSE = "Erro interno do servidor"
        const val NOT_AUTHORIZED = "Nome de usuário e/ou senha incorreto"
        const val MISSING_POP_NAME = "Nome popular é campo obrigatório"
    }

    /* Descrição textual do erro */
    private val _error = MutableStateFlow("")
    val error by this::_error

    /* Indicador de sucesso da operação */
    private val _plant : MutableSharedFlow<Plant?> = MutableSharedFlow()
    val plant = _plant.asSharedFlow()

    /* Conteúdo dos campos de texto editáveis */
    val popText : MutableStateFlow<String> by lazy { MutableStateFlow("") }
    val sciText : MutableStateFlow<String> by lazy { MutableStateFlow("") }
    val localText : MutableStateFlow<String> by lazy { MutableStateFlow("") }
    val descText : MutableStateFlow<String> by lazy { MutableStateFlow("") }


    private suspend fun validateInput(): Boolean {
        val obligatoryFields = listOf(popText)
        obligatoryFields.forEach {
            it.value.ifEmpty {
                _error.emit(ERROR.MISSING_POP_NAME)
                return false
            }
        }
        return true
    }

    fun tryRegisterPlant() {
        viewModelScope.launch {
            val plantBeginRegistered = Plant("0", popText.value, sciText.value, descText.value, localText.value)
            if (!validateInput()) return@launch
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
    }


}