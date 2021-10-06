package dev.milic.routes

import dev.milic.data.checkPasswordForEmail
import dev.milic.data.requests.AccountRequest
import dev.milic.data.responses.SimpleResponse
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route(path = "/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "A error accrued!"
                )
                return@post
            }

            val isPasswordCorrect = checkPasswordForEmail(
                email = request.email,
                passwordToCheck = request.password
            )

            if (isPasswordCorrect) {
                call.respond(
                    OK,
                    SimpleResponse(
                        successful = true,
                        message = "You are now logged in!"
                    )
                )
            } else {
                call.respond(
                    OK,
                    SimpleResponse(
                        successful = false,
                        message = "The E-Mail or password is incorrect!"
                    )
                )
            }
        }
    }
}