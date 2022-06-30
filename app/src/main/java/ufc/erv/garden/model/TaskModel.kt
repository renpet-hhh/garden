package ufc.erv.garden.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class TaskModel(private val task: suspend () -> Unit) {
    private val _busy = MutableStateFlow(false)
    val busyFlow = _busy.asStateFlow()
    val busy
        get() = busyFlow.value

    suspend fun launch() {
        if (_busy.value) return
        onTaskBegin()
        task.invoke()
        onTaskFinish()
    }
    internal val onTaskBegin = { _busy.value = true }
    internal val onTaskFinish = { _busy.value = false }


}