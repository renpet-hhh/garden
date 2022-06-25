package ufc.erv.garden.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import ufc.erv.garden.data.AuthInfo
import ufc.erv.garden.data.SettingsInfo
import ufc.erv.garden.data.readAuthInfo
import ufc.erv.garden.data.readSettingsInfo
import ufc.erv.garden.singleton.Client

open class ContextualViewModel : ViewModel() {
    protected lateinit var settings: SettingsInfo
    protected lateinit var auth: AuthInfo
    fun initialize(context: Context) {
        settings = readSettingsInfo(context)
        auth = readAuthInfo(context)
    }


    protected val client = Client.get()
}