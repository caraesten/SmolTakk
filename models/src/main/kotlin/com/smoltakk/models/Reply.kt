package com.smoltakk.models

import java.time.LocalDateTime

data class Reply(
    val topic: Topic,
    override val id: Int,
    override val author: User,
    override val body: String,
    override val posted: LocalDateTime) : Message {
    override val url = Urls.getTopicUrl(topic.id.toString())
}
