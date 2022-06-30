package ufc.erv.garden.viewModel

import androidx.lifecycle.viewModelScope
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufc.erv.garden.model.FormModel
import ufc.erv.garden.model.TaskModel
import ufc.erv.garden.singleton.Client

class LoginModel : ContextualViewModel() {
    private val vTAG = "MyPlantsModel" /* Logger TAG */

    private val _authError = MutableStateFlow("")
    val authError = _authError.asStateFlow()
    val taskModel = TaskModel {
        tryAuthenticate()
    }

    object ERROR {
        const val SERVER_CONNECTION = "Conexão ao servidor falhou"
        const val UNAUTHORIZED = "Nome de usuário e/ou senha incorreto"
        const val EMPTY = "Preencha este campo"
    }

    val formModel = FormModel(viewModelScope) {
        text("username") {
            error(0, ERROR.EMPTY)
            check { if (text.isEmpty()) 0 else null }
        }
        text("password") {
            error(0, ERROR.EMPTY)
            check { if (text.isEmpty()) 0 else null }
        }
        submit {
            viewModelScope?.launch {
                taskModel.launch()
            }
        }
    }

    private val _success : MutableSharedFlow<Unit> = MutableSharedFlow(0, 0)
    /** Indicador de sucesso da autenticação. */
    val success : SharedFlow<Unit> by this::_success

    private val _username = "mock"
    private val _password = "123456"
    private object PATH {
        const val login = "/login"
    }

    private suspend fun tryAuthenticate() {
        val username = formModel.getText("username")
        val password = formModel.getText("password")
        if (username == _username && password == _password) {
            _success.emit(Unit)
            return
        }
        Client.login(username, password)
        val result = client.runCatching {
            get(settings.server + PATH.login)
        }
        if (result.isFailure) {
            _authError.emit(ERROR.SERVER_CONNECTION)
            return
        }
        val response = result.getOrThrow()
        if (response.status != HttpStatusCode.OK) {
            _authError.emit(ERROR.UNAUTHORIZED)
            return
        }
        formModel.clearError()
        _success.emit(Unit)
    }

    private fun clearError() {
        formModel.clearError()
        _authError.value = ""
    }

}