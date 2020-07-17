package views.viewmodels

import com.smoltakk.models.Message

data class Messages<T : Message>(val messages: List<T>, val header: Message? = null) {

    // I like the explicitness of this, it makes the template easier to read :)
    val isReplies = header != null
    val isTopics = header == null

    // For when we want to show the input
    val firstTwoMessages = messages.take(2)
    val remainingMessages = messages.takeLast(messages.size - 2)
}