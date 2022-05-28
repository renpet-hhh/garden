package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginModel : ViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private object ERROR {
        const val SERVER_CONNECT = "Conexão ao servidor falhou"
        const val DECODE_RESPONSE = "Erro interno do servidor"
        const val NOT_AUTHORIZED = "Nome de usuário e/ou senha incorreto"
    }

    private val _error : MutableStateFlow<String> by lazy { MutableStateFlow("") }
    val error : StateFlow<String> by this::_error

    private val _authenticated : MutableStateFlow<Boolean> by lazy { MutableStateFlow(false) }
    val authenticated : StateFlow<Boolean> by this::_authenticated

    val username : MutableStateFlow<String> by lazy { MutableStateFlow("") }
    val password : MutableStateFlow<String> by lazy { MutableStateFlow("") }

    private val _username = "mock-user"
    private val _password = "123456"


    fun tryAuthenticate() {
        viewModelScope.launch {
            val valid = username.value == _username && password.value == _password
            delay(1500) // mock
            /* Atualiza o erro conforme a validação é feita.
            * Quando não for simplesmente um MOCK, os outros erros serão usados */
            if (!valid) _error.value = ERROR.NOT_AUTHORIZED
            _authenticated.value = valid
        }
    }

    fun clearError() {
        _error.value = ""
    }


}