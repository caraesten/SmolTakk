package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import repositories.MessagesRepository
import repositories.UserRepository
import views.*
import views.viewmodels.Messages
import web.Router
import web.Router.Companion.getTopicUrl

private const val PARAM_SUBJECT = "subject"
private const val PARAM_BODY = "body"

interface MessagesController : WithAuth {
    suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View
    suspend fun getTopicPage(topicId: Int, call: ApplicationCall): View
    suspend fun postNewTopic(roomId: Int, call: ApplicationCall): View
    suspend fun postNewReply(topicId: Int, call: ApplicationCall): View
    suspend fun carryOverTopic(topicId: Int, call: ApplicationCall): View
}

class MessagesControllerImpl(override val userRepository: UserRepository, private val messagesRepository: MessagesRepository) : MessagesController {
    override suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (roomId == -1) {
                return@withAuth Http404View(call)
            }
            val messages = messagesRepository.getActiveRoom()?.topics ?: emptyList()
            TopicsView(messages, call)
        }
    }

    override suspend fun getTopicPage(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            RepliesView(messagesRepository.getTopicById(topicId)?.replies ?: emptyList(), call)
        }
    }

    override suspend fun postNewTopic(roomId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (roomId == -1) {
                return@withAuth Http404View(call)
            }
            val params = call.receiveParameters()
            val subject = params[PARAM_SUBJECT]
            val body = params[PARAM_BODY]
            val topicId = if (!subject.isNullOrEmpty() && !body.isNullOrEmpty()) {
                messagesRepository.createTopic(params[PARAM_SUBJECT]!!, params[PARAM_BODY]!!, it).toString()
            } else ""
            RedirectView(call, getTopicUrl(topicId))
        }
    }

    override suspend fun postNewReply(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {activeUser ->
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            val params = call.receiveParameters()
            params[PARAM_BODY]?.let {
                messagesRepository.createReply(topicId, it, activeUser)
            }
            RedirectView(call, getTopicUrl(topicId.toString()))
        }
    }

    override suspend fun carryOverTopic(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }

            messagesRepository.carryOverTopic(topicId)
            RedirectView(call, getTopicUrl(topicId.toString()))
        }
    }
}
