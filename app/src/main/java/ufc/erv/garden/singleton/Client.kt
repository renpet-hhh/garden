package ufc.erv.garden.singleton

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.cookies.*

data class SessionData(
    val username: String
)

object Client {
    private var client = new()
    fun get(): HttpClient = client
    fun login(username: String, password: String) {
        client = new(username, password)
    }
    fun logout() {
        client = new()
    }
    private fun new(username: String? = null, password: String? = null) = HttpClient(OkHttp) {
        pUserName = username
        install(HttpCookies)
        if (username != null && password != null) {
            install(Auth) {
                digest {
                    credentials {
                        DigestAuthCredentials(username, password)
                    }
                    realm = "Authorization required"
                }
            }
        }
    }
    fun fakeLogin(username: String) {
        pUserName = username
    }

    private var pUserName: String? = null
    fun data() = SessionData(pUserName!!)
}