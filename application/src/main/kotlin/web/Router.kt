package web

import com.smoltakk.models.Urls.ADMIN_NEW_ROOM_PATH
import com.smoltakk.models.Urls.LOGIN_URL
import com.smoltakk.models.Urls.LOGOUT_URL
import com.smoltakk.models.Urls.PROFILE_URL
import com.smoltakk.models.Urls.ROOM_URL
import com.smoltakk.models.Urls.getTopicUrl
import controllers.*
import io.ktor.application.call
import io.ktor.http.content.resources
import io.ktor.http.content.static
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
            get(LOGOUT_URL) {
                loginController.performLogout(call).render()
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
            post() {
                adminController.createUser(call).render()
            }
            post(ADMIN_NEW_ROOM_PATH) {
                adminController.createRoom(call).render()
            }

            static("static") {
                resources("css")
            }
        }
    }
}