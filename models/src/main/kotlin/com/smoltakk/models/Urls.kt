package com.smoltakk.models

object Urls {
    const val LOGIN_URL = "/login"
    const val LOGOUT_URL = "/logout"
    const val ROOM_URL = "/"
    const val TOPIC_URL = "/topic/"
    const val PROFILE_URL = "/profile/"
    const val ADMIN_NEW_ROOM_PATH = "/admin/rooms/new"
    const val ADMIN_NEW_USER_PATH = "/admin/users/new"

    fun getTopicUrl(topicId: String) = "$TOPIC_URL$topicId"
    fun getRoomUrl(roomId: Int) = "$ROOM_URL$roomId"
    fun getProfileUrl(username: String) = "$PROFILE_URL$username"
}