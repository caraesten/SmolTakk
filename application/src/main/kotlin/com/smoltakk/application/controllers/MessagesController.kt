package com.smoltakk.application.controllers

import com.smoltakk.models.Urls.getTopicUrl
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.controllers.mixins.WithAuth
import com.smoltakk.application.di.ApplicationSingleton
import io.ktor.application.ApplicationCall
import com.smoltakk.application.views.*
import com.smoltakk.models.Message
import com.smoltakk.models.Urls.getRoomUrl
import com.smoltakk.models.User
import javax.inject.Inject

private const val PARAM_SUBJECT = "subject"
private const val PARAM_BODY = "body"

interface MessagesController : WithAuth, Controller {
    suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View
    suspend fun getTopicPage(topicId: Int, call: ApplicationCall): View
    suspend fun postNewTopic(roomId: Int, call: ApplicationCall): View
    suspend fun postNewReply(topicId: Int, call: ApplicationCall): View
    suspend fun deleteTopic(topicId: Int, call: ApplicationCall): View
    suspend fun deleteReply(replyId: Int, call: ApplicationCall): View
    suspend fun carryOverTopic(topicId: Int, call: ApplicationCall): View
}

@ApplicationSingleton
class MessagesControllerImpl @Inject constructor(override val userRepository: UserRepository, private val messagesRepository: MessagesRepository) : MessagesController {
    override suspend fun getRoomPage(roomId: Int, call: ApplicationCall): View {
        return withAuth(call) {activeUser ->
            if (roomId == -1) {
                return@withAuth Http404View(call)
            }
            val room = withTransaction(messagesRepository) {
                messagesRepository.getActiveRoom()
            }
            if (room != null && room.id == roomId) {
                TopicsView(room.topics, room, activeUser, call)
            } else if (room != null) {
                RedirectView(call, getRoomUrl(room.id))
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

    override suspend fun deleteTopic(topicId: Int, call: ApplicationCall): View {
        return withAuth(call) { loggedInUser ->
            if (topicId == -1) {
                return@withAuth Http404View(call)
            }
            withTransaction(messagesRepository) {
                val topic = messagesRepository.getTopicById(topicId)
                deleteMessage(topic, loggedInUser, call)
            }
        }
    }

    override suspend fun deleteReply(replyId: Int, call: ApplicationCall): View {
        return withAuth(call) { loggedInUser ->
            if (replyId == -1) {
                return@withAuth Http404View(call)
            }
            withTransaction(messagesRepository) {
                val topic = messagesRepository.getReplyById(replyId)
                deleteMessage(topic, loggedInUser, call)
            }
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

    private fun deleteMessage(message: Message?, loggedInUser: User, call: ApplicationCall): View {
        return if (message == null) {
            Http404View(call)
        } else {
            if (message.author == loggedInUser) {
                messagesRepository.deleteMessage(message)
                val room = messagesRepository.getActiveRoom()
                RedirectView(call, getRoomUrl(room?.id ?: 0))
            } else {
                Http403View(call)
            }
        }
    }
}
