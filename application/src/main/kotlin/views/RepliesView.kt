package views

import com.smoltakk.models.Reply
import io.ktor.application.ApplicationCall
import views.viewmodels.Messages

class RepliesView(replies: List<Reply>, call: ApplicationCall) : MessagesView<Reply>(replies, call) {
    override val templateData = Messages(replies, replies.firstOrNull()?.topic)
}
