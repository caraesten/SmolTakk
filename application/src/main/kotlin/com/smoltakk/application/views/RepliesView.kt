package com.smoltakk.application.views

import com.smoltakk.models.Reply
import com.smoltakk.models.Room
import com.smoltakk.models.Topic
import com.smoltakk.models.User
import io.ktor.application.ApplicationCall
import com.smoltakk.application.views.viewmodels.Messages

class RepliesView(replies: List<Reply>, header: Topic, activeUser: User, activeRoom: Room, call: ApplicationCall) : MessagesView<Reply>(replies, activeUser, activeRoom, call) {
    override val templateData = Messages(replies, header.id, activeUser, activeRoom, header)
}
