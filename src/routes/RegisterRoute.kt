package dev.milic.routes

import dev.milic.data.checkIfUserExists
import dev.milic.data.collections.User
import dev.milic.data.registerUser
import dev.milic.data.requests.AccountRequest
import dev.milic.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// If User is not existing create new
fun Route.registerRoute() {
    route(path = "/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(
                    status = BadRequest,
                    message = "A error accrued!"
                )
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if (!userExists) {
                if (registerUser(
                        User(
                            email = request.email,
                            password = request.password
                        )
                    )
                ) {
                    call.respond(
                        OK,
                        SimpleResponse(
                            successful = true,
                            message = "Successfully created account!"
                        )
                    )
                } else {
                    call.respond(
                        OK,
                        SimpleResponse(
                            successful = false,
                            message = "An error occurred!"
                        )
                    )
                }
            } else {
                call.respond(
                    OK,
                    SimpleResponse(
                        successful = false,
                        message = "A User with that E-Mail already exists."
                    )
                )
            }
        }
    }
}