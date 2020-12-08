package com.smoltakk.models

object Urls {
    const val LOGIN_URL = "/login"
    const val LOGOUT_URL = "/logout"
    const val ROOM_URL = "/"
    const val TOPIC_URL = "/topic/"
    const val REPLY_URL = "/reply/"
    const val PROFILE_URL = "/profile/"
    const val ADMIN_NEW_ROOM_PATH = "/admin/rooms/new"
    const val ADMIN_NEW_USER_PATH = "/admin/users/new"
    const val REVIVE_URL = "/revive/"

    const val DELETE_VERB = "/delete"

    fun getTopicUrl(topicId: String) = "$TOPIC_URL$topicId"
    fun getRoomUrl(roomId: Int) = "$ROOM_URL$roomId"
    fun getProfileUrl(username: String) = "$PROFILE_URL$username"
    fun getReviveUrl(topicId: String) = "$REVIVE_URL$topicId"
    fun getTopicDeleteUrl(topicId: String) = "$TOPIC_URL$topicId$DELETE_VERB"
    fun getReplyDeleteUrl(replyId: String) = "$REPLY_URL$replyId$DELETE_VERB"
}