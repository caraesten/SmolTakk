package views

import io.ktor.application.ApplicationCall
import models.Reply
import models.Topic
import views.viewmodels.Messages

class RepliesView(replies: List<Reply>, call: ApplicationCall) : MessagesView<Reply>(replies, call) {
    override val templateData = Messages(replies, replies.firstOrNull()?.topic)
}
