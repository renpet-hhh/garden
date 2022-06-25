package ufc.erv.garden.singleton

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.cookies.*

object Client {
    private var client = new()
    fun get(): HttpClient = client
    fun restart() {
        client = new()
    }
    fun logout() {
        restart()
    }
    private fun new() = HttpClient(OkHttp) {
        install(HttpCookies)
    }

}