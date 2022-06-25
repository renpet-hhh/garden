package ufc.erv.garden.data

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import kotlinx.serialization.Serializable
import ufc.erv.garden.R

/** Representa as informações no SharedPreferences padrão */
@Serializable
data class SettingsInfo(
    /** Endereço do servidor */
    val server: String,
    /** Indica se o modo escuro está ativado */
    val darkMode: Boolean,
)

fun readSettingsInfo(context: Context): SettingsInfo {
    val settings = PreferenceManager.getDefaultSharedPreferences(context)
    val server = settings.getString("server", "mock") ?: "mock"
    val darkMode = settings.getBoolean("darkMode", true)
    return SettingsInfo(server, darkMode)
}
fun SettingsInfo.writeSettingsInfo(context: Context) {
    val settings = PreferenceManager.getDefaultSharedPreferences(context)
    settings.edit() {
        putString("server", server)
        putBoolean("darkMode", darkMode)
    }
}