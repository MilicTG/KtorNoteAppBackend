package dev.milic.data.requests

data class AddOwnerRequest(
    val noteId: String,
    val ownersEmail: String
)
