package models

import java.time.LocalDateTime

data class Reply(
    override val author: User,
    override val body: String,
    override val posted: LocalDateTime) : Message