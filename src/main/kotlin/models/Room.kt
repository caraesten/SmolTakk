package models

import java.time.LocalDateTime

data class Room(val topics: List<Topic>, val created: LocalDateTime, val id: Int)
