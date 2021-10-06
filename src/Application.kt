package dev.milic

import dev.milic.data.checkPasswordForEmail
import dev.milic.routes.loginRoute
import dev.milic.routes.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    //Some info about server
    install(DefaultHeaders)
    //Log all HTTP requests and responses
    install(CallLogging)
    //Define URL endpoints
    install(Routing) {
        registerRoute()
        loginRoute()
    }
    //Define a response type JSON
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication) {
        configureAuth()
    }
}

private fun Authentication.Configuration.configureAuth() {
    basic {
        realm = "Note Server"
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password

            if (checkPasswordForEmail(
                    email = email,
                    passwordToCheck = password
                )
            ) {
                UserIdPrincipal(name = email)
            } else null
        }
    }
}

