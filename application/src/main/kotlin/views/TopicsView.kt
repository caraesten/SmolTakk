package views

import com.smoltakk.models.Room
import io.ktor.application.ApplicationCall
import com.smoltakk.models.Topic
import com.smoltakk.models.User
import views.viewmodels.Messages

class TopicsView(topics: List<Topic>, room: Room, activeUser: User, call: ApplicationCall) : MessagesView<Topic>(topics, activeUser, room, call) {
    override val templateData = Messages(topics, room.id, activeUser, room)
}
