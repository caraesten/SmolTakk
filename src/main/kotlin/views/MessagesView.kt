package views

import io.ktor.application.ApplicationCall
import models.Message
import views.viewmodels.Messages

abstract class MessagesView<T : Message>(val messages: List<T>, call: ApplicationCall) : MustacheView<Messages<T>>(call) {
    override val templatePath = "messages.html"
}
