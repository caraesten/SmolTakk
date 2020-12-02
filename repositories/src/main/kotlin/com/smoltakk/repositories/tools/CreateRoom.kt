package com.smoltakk.repositories.tools

object CreateRoom {
    fun run() {
        val messagesRepo = MessagesTools().getMessagesRepo()
        messagesRepo.withTransaction {
            messagesRepo.createRoom()
        }
    }
}