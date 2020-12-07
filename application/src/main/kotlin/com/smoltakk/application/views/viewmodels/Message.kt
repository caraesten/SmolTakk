package com.smoltakk.application.views.viewmodels

import com.smoltakk.application.views.utils.ScriptFilters
import com.smoltakk.models.Message
import com.smoltakk.models.Topic
import com.smoltakk.models.User
import java.time.LocalDateTime

data class Message<T : Message>(val underlying: T) {
    val body: String by underlying::body
    val author: User by underlying::author
    val posted: LocalDateTime by underlying::posted
    val url: String by underlying::url

    val replyCount = if (underlying is Topic) { underlying.replyCount } else { 0 }
    val replies = if (underlying is Topic) { underlying.replies } else { emptyList() }

    val bodyNoScript = ScriptFilters.escapeTag(body)
    val containsScripts = ScriptFilters.containsTag(body)

    val repliesContainScripts = replies.any { ScriptFilters.containsTag(it.body) }
}