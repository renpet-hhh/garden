package ufc.erv.garden.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class TaskModelTest {
    private lateinit var model: TaskModel
    private var valueChangedByTask = false

    @Before
    fun initialize() {
        valueChangedByTask = false
        model = TaskModel {
            valueChangedByTask = true
        }
    }

    @Test
    fun hasCorrectDefault() {
        assertEquals(false, model.busy)
    }

    @Test
    fun getsBusyOnTask() {
        model.onTaskBegin()
        assertEquals(true, model.busy)
    }

    @Test
    fun getsBusyAfterTask() {
        model.onTaskBegin()
        model.onTaskFinish()
        assertEquals(false, model.busy)
    }

    @Test
    fun doesNotExecuteTaskIfNotLaunched() {
        assertEquals(false, valueChangedByTask)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun executesTask() = runTest {
        model.launch()
        assertEquals(true, valueChangedByTask)
    }


}