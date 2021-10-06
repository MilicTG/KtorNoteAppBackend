package dev.milic.routes

import dev.milic.data.getNotesForUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotesForUser(email = email)

                //pass notes as JSON
                call.respond(
                    OK,
                    message = notes
                )
            }
        }
    }
}