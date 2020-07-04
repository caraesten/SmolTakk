package models

import java.time.LocalDateTime

interface Message {
    val body: String
    val author: User
    val posted: LocalDateTime
}
