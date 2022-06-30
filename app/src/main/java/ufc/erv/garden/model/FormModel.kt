package ufc.erv.garden.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow


class FormModel(val viewModelScope: CoroutineScope? = null, builder: Builder.() -> Unit) {

    private val textFields = mutableMapOf<String, FieldModel>()
    private var submitAction: FormModel.() -> Unit = { }
    private var failedSubmitAction: FormModel.(String) -> Unit = { }

    val valid
        get() = textFields.values.all { it.valid }

    class Builder(private val model: FormModel) {
        fun text(name: String, builder: FieldModel.Builder.() -> Unit) {
            model.textFields[name] = FieldModel(model.viewModelScope, builder)
        }
        fun submit(action: FormModel.() -> Unit) {
            model.submitAction = action
        }
        fun onFailedSubmitTry(action: FormModel.(String) -> Unit) {
            model.failedSubmitAction = action
        }
    }

    init {
        Builder(this).apply(builder)
    }

    fun saveIfValid() {
        val reason = findErrorReason()
        if (reason == null) textFields.values.forEach { it.saveIfValid() }
        else onFailedSubmit(reason)
    }
    fun resetToPrevious() {
        textFields.values.forEach { it.resetToPrevious() }
    }
    fun clear() {
        textFields.values.forEach { it.clear() }
    }
    fun clearError() {
        textFields.values.forEach { it.clearError() }
    }
    fun warn() {
        textFields.values.forEach{ it.warn() }
    }
    fun submit() {
        val reason = findErrorReason()
        if (reason == null) this.apply(submitAction)
        else onFailedSubmit(reason)
    }
    private fun findErrorReason(): String? {
        warn()
        textFields.values.forEach {
            if (it.errorMessage != null) return it.errorMessage
        }
        return null
    }
    private fun onFailedSubmit(reason: String) {
        failedSubmitAction.invoke(this, reason)
    }

    fun getText(name: String) = textFields[name]?.text
    fun getFlow(name: String) = textFields[name]?.textFlow

    internal fun getMutableFlow(name: String): MutableStateFlow<String>? {
        return textFields[name]?.getMutableFlow()
    }
}