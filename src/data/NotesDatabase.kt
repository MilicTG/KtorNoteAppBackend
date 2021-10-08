package dev.milic.data

import dev.milic.data.collections.Note
import dev.milic.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

//eq is equals
//Goes to entire document and compare email
suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(filter = User::email eq email) != null
}

//Check if user with password exist or return false
suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == passwordToCheck
}

//Get all Notes for User
suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notes.findOneById(id = note.id) != null
    return if (noteExists) {
        notes.updateOneById(id = note.id, update = note).wasAcknowledged()
    } else {
        notes.insertOne(document = note).wasAcknowledged()
    }
}
