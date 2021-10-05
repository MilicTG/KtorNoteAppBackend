package dev.milic

import dev.milic.data.collections.User
import dev.milic.data.registerUser
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    //Some info about server
    install(DefaultHeaders)
    //Log all HTTP requests and responses
    install(CallLogging)
    //Define URL endpoints
    install(Routing)
    //Define a response type JSON
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    CoroutineScope(Dispatchers.IO).launch {
        registerUser(
            User(
                email = "abc@abc.com",
                password = "123456"
            )
        )
    }
}

