package ufc.erv.garden.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FieldModel(viewModelScope: CoroutineScope? = null, builder: Builder.() -> Unit) {

    private val _textFlow = MutableStateFlow("")
    private val _errorFlow = MutableStateFlow<String?>(null)
    private var _validate: FieldModel.() -> Int? = { null }
    private var _savedText = ""
    private var onTextChange: (String) -> Unit = { clearError() }
    private val allErrors = mutableMapOf<Int, String>()

    val textFlow = _textFlow.asStateFlow()
    val text
        get() = textFlow.value

    /** Mensagem de erro atual. A mensagem de erro é atualizada automaticamente
     * quando se tenta salvar o campo atual.
     * Para forçar a atualização, use warn() */
    val errorMessage
        get() = _errorFlow.value
    /** Indica se o texto atual é válido */
    val valid
        get() = this.run(_validate) == null


    class Builder(private val model: FieldModel) {
        fun setDefaultText(text: String) {
            model._textFlow.value = text
            model.saveIfValid()
        }

        fun error(id: Int, message: String) {
            model.allErrors[id] = message
        }
        fun check(validate: FieldModel.() -> Int?) {
            model._validate = validate
        }
        fun overwriteOnTextChange(listener: (String) -> Unit) {
            model.onTextChange = listener
        }
    }

    init {
        Builder(this).apply(builder)
        viewModelScope?.launch {
            _textFlow.collect {
                onTextChange(it)
            }
        }
    }

    fun clearError() = updateErrorMessage(null)
    private fun updateErrorMessage(message: String?) {
        _errorFlow.value = message
    }

    /** Atualiza a mensagem de erro do estado atual.
     * Isso dá a chance para que os usuários desta classe sejam notificados
     * dos erros apenas quando eles quiserem */
    fun warn() {
        val errorId = this.run(_validate)
        if (errorId == null) {
            clearError()
            return
        }
        updateErrorMessage(allErrors[errorId])
    }

    fun saveIfValid() {
        if (valid) _savedText = text
        else warn()
    }
    fun resetToPrevious() {
        _textFlow.value = _savedText
        clearError()
    }
    fun clear() {
        _textFlow.value = ""
        clearError()
    }


}