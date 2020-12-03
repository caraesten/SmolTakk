package com.smoltakk.repositories.tools

import com.smoltakk.models.Room
import java.time.LocalDateTime
import java.time.Period

object CreateRoom {
    fun run(checkTime: Boolean = false) {
        val messagesRepo = MessagesTools().getMessagesRepo()
        if (checkTime) {
            val activeRoom = messagesRepo.getActiveRoom(false)!!
            val timeRemaining = java.time.Duration.between(LocalDateTime.now(), activeRoom.created + Period.ofDays(Room.ROOM_TTL_DAYS))
            if (!timeRemaining.isNegative) {
                return
            }
        }
        messagesRepo.withTransaction {
            messagesRepo.createRoom()
        }
    }
}