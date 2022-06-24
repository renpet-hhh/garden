package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginModel : ViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private object ERROR {
        const val SERVER_CONNECT = "Conexão ao servidor falhou"
        const val DECODE_RESPONSE = "Erro interno do servidor"
        const val NOT_AUTHORIZED = "Nome de usuário e/ou senha incorreto"
    }

    private val _error : MutableStateFlow<String> = MutableStateFlow("")
    val error : StateFlow<String> by this::_error


    private val _cookie : MutableSharedFlow<String?> = MutableSharedFlow(0, 0)
    /** Indicador de sucesso da autenticação.
    Quando uma String for emitida, ela é o cookie que autentica uma sessão e deve
    ser enviado no cabeçalho Cookie nas próximas requisições.
    Quando null for emitido, a autenticação falhou. */
    val cookie : SharedFlow<String?> by this::_cookie

    val username : MutableStateFlow<String> = MutableStateFlow("")
    val password : MutableStateFlow<String> = MutableStateFlow("")

    private val _username = "mock"
    private val _password = "123456"
    private object PATH {
        const val login = "/login"
    }
    lateinit var server: String


    fun tryAuthenticate() {
        viewModelScope.launch {
            // cookie enviado pelo servidor para garantir esta sessão
            // atualmente, é apenas um MOCK
            val serverCookie = "zz-mock-cookie-123456789"
            val valid = username.value == _username && password.value == _password
            if (valid) {
                _cookie.emit(serverCookie)
                return@launch
            }
            val client = HttpClient(OkHttp) {
                developmentMode = true
                install(Auth) {
                    digest {
                        credentials {
                            DigestAuthCredentials(this@LoginModel.username.value, this@LoginModel.password.value)
                        }
                        realm = "Authorization required"
                    }
                }
            }
            val result = client.runCatching {
                get(server + PATH.login)
            }
            if (result.isFailure) {
                _error.emit(ERROR.SERVER_CONNECT)
                return@launch
            }
            val response = result.getOrThrow()
            if (response.status != HttpStatusCode.OK) {
                _error.emit(ERROR.NOT_AUTHORIZED)
                _cookie.emit(null)
                return@launch
            }
            clearError()
            _cookie.emit(serverCookie)
        }
    }

    fun clearError() {
        _error.value = ""
    }


}