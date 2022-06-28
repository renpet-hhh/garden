package ufc.erv.garden.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import ufc.erv.garden.data.SettingsInfo
import ufc.erv.garden.data.readSettingsInfo
import ufc.erv.garden.singleton.Client
import ufc.erv.garden.singleton.SessionData

open class ContextualViewModel : ViewModel() {
    protected lateinit var settings: SettingsInfo
    fun initialize(context: Context) {
        settings = readSettingsInfo(context)
    }


    protected val client: HttpClient
        get() = Client.get()
    val auth: SessionData
        get() = Client.data()
}