package ufc.erv.garden.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FieldModelTest {
    private lateinit var model: FieldModel
    private val errorMessage0 = "Nome deve possuir no mínimo 3 caracteres"
    private val errorMessage1 = "Nome não pode conter caractere . ou /"
    private val defaultText = "Padrão"
    private val loadedText = "Loaded"

    @Before
    fun initialize() {
        initializeWithText(defaultText)
    }
    private fun initializeWithText(defaultText: String) {
        model = FieldModel {
            setDefaultText(defaultText)
            error(0, errorMessage0)
            error(1, errorMessage1)
            check {
                when {
                    text.length < 3 -> 0
                    '.' in text || '/' in text -> 1
                    else -> null
                }
            }
            load {
                delay(1)
                loadedText
            }
        }
    }

    private fun changeText(text: String) {
        val textFlow = model.getMutableFlow()
        textFlow.value = text
        model.onTextChange(text) // simula o evento onTextChange
    }
    private fun trySaveText(text: String) {
        changeText(text)
        model.saveIfValid()
    }


    @Test
    fun isCorrectlyInitializedWhenDefaultTextIsValid() {
        assertEquals(defaultText, model.text)
        assertEquals(true, model.valid)
        assertEquals(null, model.errorMessage)
    }

    @Test
    fun isCorrectlyInitializedWhenDefaultTextIsInvalid() {
        initializeWithText("Hel/lo")
        assertEquals("Hel/lo", model.text)
        assertEquals(false, model.valid)
        assertEquals(null, model.errorMessage)
    }

    @Test
    fun updatesValidity() {
        changeText("oi")
        assertEquals(false, model.valid)
    }

    @Test
    fun updatesErrorMessage() {
        changeText("oi")
        model.warn()
        assertEquals(errorMessage0, model.errorMessage)
    }

    @Test
    fun updatesAnotherErrorMessage() {
        changeText("tudo.")
        model.warn()
        assertEquals(errorMessage1, model.errorMessage)
    }

    @Test
    fun updatesErrorWhenTryingToSave() {
        trySaveText("tudo.")
        assertEquals(errorMessage1, model.errorMessage)
    }

    @Test
    fun doesNotUpdateErrorMessageOnEveryChange() {
        changeText("oi")
        assertEquals(null, model.errorMessage)
    }

    @Test
    fun resetsToPreviousTextWhenValid() {
        trySaveText("saved")
        changeText("other")
        model.resetToPrevious()
        assertEquals("saved", model.text)
    }

    @Test
    fun doesNotResetToPreviousTextWhenInvalid() {
        trySaveText("oi.")
        model.resetToPrevious()
        assertEquals(defaultText, model.text)
    }

    @Test
    fun handlesValidityOnResetToPrevious() {
        trySaveText("hello/")
        model.resetToPrevious()
        assertEquals(true, model.valid)
    }

    @Test
    fun handlesErrorMessageOnResetToPrevious() {
        trySaveText("hello/")
        model.resetToPrevious()
        assertEquals(null, model.errorMessage)
    }

    @Test
    fun clearsText() {
        model.clear()
        assertEquals("", model.text)
    }

    @Test
    fun errorIsClearedWhenTextIsCleared() {
        trySaveText("oi")
        model.clear()
        assertEquals(null, model.errorMessage)
    }

    @Test
    fun errorIsClearedWhenTextChanges() {
        trySaveText("oi")
        changeText("o")
        assertEquals(null, model.errorMessage)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadsData() = runTest {
        model.refresh()
        assertEquals(loadedText, model.text)
    }

}