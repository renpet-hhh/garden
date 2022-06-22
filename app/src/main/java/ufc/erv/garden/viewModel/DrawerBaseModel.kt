package ufc.erv.garden.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class DrawerBaseModel : ViewModel() {
    private val _visible = MutableSharedFlow<Boolean>(1)
    val visible = _visible.asSharedFlow()

    fun showMenu() {
        viewModelScope.launch {
            _visible.emit(true)
        }
    }
    fun hideMenu() {
        viewModelScope.launch {
            _visible.emit(false)
        }
    }
    fun toggleMenu() {
        viewModelScope.launch {
            if (_visible.replayCache.isNotEmpty()) {
                _visible.emit(_visible.replayCache.last())
            }
        }
    }
}