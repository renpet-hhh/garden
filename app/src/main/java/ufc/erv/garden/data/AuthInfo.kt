package ufc.erv.garden.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import ufc.erv.garden.R

import kotlinx.serialization.Serializable

/** Representa as informações no SharedPreferences de autenticação */
@Serializable
data class AuthInfo(
    /** Nome do usuário */
    val username: String,
    /** Cookie que identifica este usuário no servidor */
    val cookie: String,
)

fun readAuthInfo(context: Context): AuthInfo {
    val auth = context.getSharedPreferences(
        context.resources.getString(R.string.auth_shared_preferences),
        MODE_PRIVATE
    )
    val username = auth.getString("username", "mock") ?: "mock"
    val cookie = auth.getString("cookie", "mock-cookie") ?: "mock-cookie"
    return AuthInfo(username, cookie)
}
fun AuthInfo.writeAuthInfo(context: Context) {
    val auth = context.getSharedPreferences(
        context.resources.getString(R.string.auth_shared_preferences),
        MODE_PRIVATE
    )
    auth.edit() {
        putString("username", username)
        putString("cookie", cookie)
    }
}