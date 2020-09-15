package com.smoltakk.models

import java.time.LocalDateTime

data class Room(val topics: List<Topic>, val created: LocalDateTime, val id: Int) {
    companion object {
        const val ROOM_TTL_DAYS = 3
    }
}
