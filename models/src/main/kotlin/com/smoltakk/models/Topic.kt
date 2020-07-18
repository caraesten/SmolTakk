package com.smoltakk.models

import java.time.LocalDateTime

data class Topic(
    val title: String,
    val id: Int,
    override val body: String,
    override val author: User,
    override val posted: LocalDateTime,
    val replies: List<Reply> = emptyList(),
    val replyCount: Int) : Message {
    override val url = Urls.getTopicUrl(id.toString())
}
