package controllers

import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import views.*
import web.Router.Companion.getTopicUrl

private const val PARAM_SUBJECT = "subject"
private const val PARAM_BODY = "body"

interface MessagesController : WithAuth, Controller {
    suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View
    suspend fun getTopicPage(topicId: Int, call: ApplicationCall): View
    suspend fun postNewTopic(roomId: Int, call: ApplicationCall): View
    suspend fun postNewReply(topicId: Int, call: ApplicationCall): View
    suspend fun carryOverTopic(topicId: Int, call: ApplicationCall): View
}

class MessagesControllerImpl(override val userRepository: UserRepository, private val messagesRepository: MessagesRepository) : MessagesController {
    override suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View {
        return withAuth(call) {activeUser ->
            if (roomId == -1) {
                return@withAuth Http404View(call)
            }
            val room = withTransaction(messagesRepository) {
                messagesRepository.getActiveRoom()
            }
            if (room != null) {
                TopicsView(room.topics, room, activeUser, call)
            } else {
                Http404View(call)
            }
        }
    }

    override suspend fun getTopicPage(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {activeUser ->
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            val (topic, activeRoom) = withTransaction(messagesRepository) {
                Pair(messagesRepository.getTopicById(topicId), messagesRepository.getActiveRoom(false))
            }
            if (topic == null || activeRoom == null) {
                Http404View(call)
            } else {
                RepliesView(topic.replies, topic, activeUser, activeRoom, call)
            }
        }
    }

    override suspend fun postNewTopic(roomId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (roomId == -1) {
                return@withAuth Http404View(call)
            }
            val params = call.getParametersOrNull()
            val subject = params?.get(PARAM_SUBJECT)
            val body = params?.get(PARAM_BODY)
            val topicId = if (!subject.isNullOrEmpty() && !body.isNullOrEmpty()) {
                withTransaction(messagesRepository) {
                    messagesRepository.createTopic(params[PARAM_SUBJECT]!!, params[PARAM_BODY]!!, it).toString()
                }
            } else ""
            RedirectView(call, getTopicUrl(topicId))
        }
    }

    override suspend fun postNewReply(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {activeUser ->
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            val params = call.getParametersOrNull()
            params?.get(PARAM_BODY)?.let {
                withTransaction(messagesRepository) {
                    messagesRepository.createReply(topicId, it, activeUser)
                }
            }
            RedirectView(call, getTopicUrl(topicId.toString()))
        }
    }

    override suspend fun carryOverTopic(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) {
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            withTransaction(messagesRepository) {
                messagesRepository.carryOverTopic(topicId)
            }
            RedirectView(call, getTopicUrl(topicId.toString()))
        }
    }
}
