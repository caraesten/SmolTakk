package views

import io.ktor.application.ApplicationCall
import com.smoltakk.models.Topic
import views.viewmodels.Messages

class TopicsView(topics: List<Topic>, call: ApplicationCall) : MessagesView<Topic>(topics, call) {
    override val templateData = Messages(topics)
}
