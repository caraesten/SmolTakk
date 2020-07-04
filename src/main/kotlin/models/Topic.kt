package models

import java.time.LocalDateTime

data class Topic(
    val title: String,
    override val body: String,
    override val author: User,
    override val posted: LocalDateTime,
    val replies: List<Reply> = emptyList()) : Message