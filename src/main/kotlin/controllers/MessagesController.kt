package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import repositories.MessagesRepository
import repositories.UserRepository
import views.View

interface MessagesController : WithAuth {
    fun getRoomPage(roomId: Int, call: ApplicationCall): View
    fun getTopicPage(topicId: Int, call: ApplicationCall): View
    fun postNewTopic(roomId: Int, call: ApplicationCall): View
    fun postNewReply(topicId: Int, call: ApplicationCall): View
}

class MessagesControllerImpl(override val userRepository: UserRepository, private val messagesRepository: MessagesRepository) : MessagesController {
    override fun getRoomPage(roomId: Int, call: ApplicationCall): View {

    }

    override fun getTopicPage(topicId: Int, call: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun postNewTopic(call: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun postNewReply(call: ApplicationCall): View {
        TODO("Not yet implemented")
    }
}
