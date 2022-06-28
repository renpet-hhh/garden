package ufc.erv.garden.viewModel

import androidx.lifecycle.viewModelScope
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ufc.erv.garden.singleton.Client

class LoginModel : ContextualViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private object ERROR {
        const val SERVER_CONNECT = "Conexão ao servidor falhou"
        const val DECODE_RESPONSE = "Erro interno do servidor"
        const val NOT_AUTHORIZED = "Nome de usuário e/ou senha incorreto"
    }

    private val _error : MutableStateFlow<String> = MutableStateFlow("")
    val error : StateFlow<String> by this::_error

    /** Indica se está aguardando resposta do servidor */
    private val _fetching = MutableStateFlow(false)
    val fetching by this::_fetching

    private val _success : MutableSharedFlow<Unit> = MutableSharedFlow(0, 0)
    /** Indicador de sucesso da autenticação. */
    val success : SharedFlow<Unit> by this::_success

    val usernameField : MutableStateFlow<String> = MutableStateFlow("")
    val passwordField : MutableStateFlow<String> = MutableStateFlow("")

    private val _username = "mock"
    private val _password = "123456"
    private object PATH {
        const val login = "/login"
    }

    fun tryAuthenticate() {
        _fetching.value = true
        viewModelScope.launch {
            val valid = usernameField.value == _username && passwordField.value == _password
            if (valid) {
                _success.emit(Unit)
                return@launch
            }
            Client.login(usernameField.value, passwordField.value)
            val result = client.runCatching {
                get(settings.server + PATH.login)
            }
            if (result.isFailure) {
                _error.emit(ERROR.SERVER_CONNECT)
                return@launch
            }
            val response = result.getOrThrow()
            if (response.status != HttpStatusCode.OK) {
                _error.emit(ERROR.NOT_AUTHORIZED)
                return@launch
            }
            clearError()
            _success.emit(Unit)
        }.invokeOnCompletion {
            _fetching.value = false
        }
    }

    fun clearError() {
        _error.value = ""
    }


}