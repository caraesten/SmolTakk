package views

import com.smoltakk.models.Message
import com.smoltakk.models.Room
import com.smoltakk.models.User
import io.ktor.application.ApplicationCall
import views.viewmodels.Messages

abstract class MessagesView<T : Message>(val messages: List<T>, val activeUser: User, val activeRoom: Room, call: ApplicationCall) : MustacheView<Messages<T>>(call) {
    override val templatePath = "messages.html"
}
