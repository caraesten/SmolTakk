package web

import controllers.*
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post

class Router(
    private val homePageController: HomePageController,
    private val messagesController: MessagesController,
    private val profileController: ProfileController,
    private val loginController: LoginController,
    private val adminController: AdminController
) {
    fun configureRouting(routes: Routing) {
        routes {
            get("/") {
                homePageController.getHomePage(call).render()
            }
            get("$ROOM_URL{room_id}") {
                messagesController.getRoomPage(call.parameters["room_id"]?.toIntOrNull() ?: -1, call).render()
            }
            post("$ROOM_URL{room_id}") {
                messagesController.postNewTopic(call.parameters["room_id"]?.toIntOrNull() ?: -1, call).render()
            }

            get("$PROFILE_URL{username}") {
                profileController.getProfile(call, call.parameters["username"]).render()
            }
            post("$PROFILE_URL{username}") {
                profileController.updateProfile(call, call.parameters["username"]).render()
            }

            get(LOGIN_URL) {
                loginController.getLoginPage(call).render()
            }
            post(LOGIN_URL) {
                loginController.performLogin(call).render()
            }

            get(getTopicUrl("{topic_id}")) {
                messagesController.getTopicPage(call.parameters["topic_id"]?.toIntOrNull() ?: -1, call).render()
            }
            post(getTopicUrl("{topic_id}")) {
                messagesController.postNewReply(call.parameters["topic_id"]?.toIntOrNull() ?: -1, call).render()
            }
            get("${getTopicUrl("{topic_id}")}/carryover") {
                messagesController.carryOverTopic(call.parameters["topic_id"]?.toIntOrNull() ?: -1, call).render()
            }

            get("/admin/users") {
                adminController.showUserAdminPage(call).render()
            }
            get("/admin/rooms") {
                adminController.showRoomAdminPage(call).render()
            }
            post("/admin/users/new") {
                adminController.createUser(call).render()
            }
            post("/admin/rooms/new") {
                adminController.createRoom(call).render()
            }
        }
    }

    companion object{
        const val LOGIN_URL = "/login"
        const val ROOM_URL = "/"
        const val TOPIC_URL = "/topic/"
        const val PROFILE_URL = "/profile/"

        fun getTopicUrl(topicId: String) = "$TOPIC_URL$topicId"
        fun getRoomUrl(roomId: Int) = "$ROOM_URL$roomId"
        fun getProfileUrl(username: String) = "$PROFILE_URL$username"
    }
}