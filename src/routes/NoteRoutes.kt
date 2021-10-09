package dev.milic.routes

import dev.milic.data.*
import dev.milic.data.collections.Note
import dev.milic.data.requests.AddOwnerRequest
import dev.milic.data.requests.DeleteNoteRequest
import dev.milic.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes() {
    route(path = "/getNotes") {
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
    route(path = "/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }

                if (saveNote(note)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
    route(path = "/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<AddOwnerRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (!checkIfUserExists(request.ownersEmail)) {
                    call.respond(
                        OK,
                        SimpleResponse(
                            successful = false,
                            message = "No User with this E-Mail exists!"
                        )
                    )
                    return@post
                }
                if (isOwnerOfNote(noteId = request.noteId, ownersEmail = request.ownersEmail)) {
                    call.respond(
                        OK,
                        SimpleResponse(
                            successful = false,
                            message = "This User is already an owner of this note."
                        )
                    )
                    return@post
                }
                if (addOwnerToNote(noteId = request.noteId, ownersEmail = request.ownersEmail)) {
                    call.respond(
                        OK,
                        SimpleResponse(
                            successful = true,
                            message = "${request.ownersEmail} can now see this note."
                        )
                    )
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
    route(path = "/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if (deleteNoteForUser(email = email, noteId = request.id)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
}