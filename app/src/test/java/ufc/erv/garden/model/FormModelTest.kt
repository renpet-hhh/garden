package ufc.erv.garden.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals


class FormModelTest {
    private lateinit var model: FormModel

    private val smallNameError = "Nome deve ter no mínimo 3 caracteres"
    private val bigNameError = "Nome deve ter no máximo 10 caracteres"
    private val defaultName = "Padrão"
    private val defaultEmail = "test@gmail.com"

    private val invalidEmail = "invalid_email"
    private val validEmail = "pet@gmail.com"

    private val emailError = "Deve conter @"

    private var submittedName = ""
    private var submittedEmail = ""
    private var failedReason = ""

    @Before
    fun initialize() {
        submittedName = ""
        submittedEmail = ""
        failedReason = ""
        model = FormModel {
            text("name") {
                error(0, smallNameError)
                error(1, bigNameError)
                setDefaultText(defaultName)
                check {
                    when {
                        text.length < 3 -> 0
                        text.length > 10 -> 1
                        else -> null
                    }
                }
            }
            text("email") {
                error(0, emailError)
                setDefaultText(defaultEmail)
                check {
                    if ('@' in text) null else 0
                }
            }
            submit {
                submittedName = getText("name")
                submittedEmail = getText("email")
            }
            onFailedSubmitTry { reason ->
                failedReason = reason
            }
        }
    }

    private fun changeText(inputName: String, text: String) {
        model.getMutableFlow(inputName)?.value = text
    }

    @Test
    fun setsDefaultText() {
        assertEquals(defaultName, model.getText("name"))
        assertEquals(defaultEmail, model.getText("email"))
    }

    @Test
    fun changesText() {
        changeText("name", "oi")
        assertEquals("oi", model.getText("name"))
    }

    @Test
    fun isValidWhenItShould() {
        changeText("name", "olá")
        assertEquals(true, model.valid)
    }

    @Test
    fun isInvalidWhenItShould1() {
        changeText("name", "12345678901")
        assertEquals(false, model.valid)
    }
    @Test
    fun isInvalidWhenItShould2() {
        changeText("email", invalidEmail)
        assertEquals(false, model.valid)
    }

    @Test
    fun restoresValidity() {
        changeText("name", "oi")
        changeText("name", "olá")
        assertEquals(true, model.valid)
    }

    @Test
    fun clearsText() {
        model.clear()
        assertEquals("", model.getText("name"))
        assertEquals("", model.getText("email"))
    }

    @Test
    fun handlesValidityOnClearText() {
        model.clear()
        assertEquals(false, model.valid)
    }

    @Test
    fun resetsToPreviousSavedState() {
        changeText("name", "hello")
        changeText("email", validEmail)
        model.resetToPrevious()
        assertEquals(defaultName, model.getText("name"))
        assertEquals(defaultEmail, model.getText("email"))
    }

    @Test
    fun savesStateIfValid() {
        changeText("name", "hello")
        changeText("email", validEmail)
        model.saveIfValid()
        changeText("name", "olá")
        model.resetToPrevious()
        assertEquals("hello", model.getText("name"))
    }

    @Test
    fun doesNotSaveStateIfInvalid() {
        changeText("name", "oi")
        changeText("email", validEmail)
        model.saveIfValid()
        model.resetToPrevious()
        assertEquals(defaultName, model.getText("name"))
        assertEquals(defaultEmail, model.getText("email"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun submitIsPerformedOnCurrentValidData() = runTest {
        changeText("name", "olá")
        changeText("email", validEmail)
        model.syncSubmit()
        assertEquals("olá", submittedName)
        assertEquals(validEmail, submittedEmail)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun submitIsNotPerformedOnCurrentInvalidData() = runTest {
        changeText("name", "oi")
        changeText("email", validEmail)
        model.syncSubmit()
        assertEquals("", submittedName)
        assertEquals("", submittedEmail)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun sendsCorrectReasonOnSubmitFail() = runTest {
        changeText("name", "12345678901")
        model.syncSubmit()
        assertEquals(bigNameError, failedReason)
    }

    // a razão esperada é a do primeiro input declarado ("name")
    @ExperimentalCoroutinesApi
    @Test
    fun sendsCorrectReasonOnSubmitFailOnMultipleErrors() = runTest {
        changeText("name", "12345678901")
        changeText("email", invalidEmail)
        model.syncSubmit()
        assertEquals(bigNameError, failedReason)
    }


}