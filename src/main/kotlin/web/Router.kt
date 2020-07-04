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
            get("/{room_id}") {
                messagesController.getRoomPage(call.parameters["room_id"]?.toIntOrNull() ?: -1, call).render()
            }
            post("/{room_id}") {
                messagesController.postNewTopic(call.parameters["room_id"]?.toIntOrNull() ?: -1, call).render()
            }

            get("/profile") {
                profileController.getProfile(call).render()
            }
            post("/profile") {
                profileController.updateProfile(call).render()
            }

            get("/login") {
                loginController.getLoginPage(call).render()
            }
            post("/login") {
                loginController.performLogin(call).render()
            }

            get("/topic/{topic_id}") {
                messagesController.getTopicPage(call.parameters["topic_id"]?.toIntOrNull() ?: -1, call).render()
            }
            post("/topic/{topic_id}") {
                messagesController.postNewReply(call.parameters["topic_id"]?.toIntOrNull() ?: -1, call).render()
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
}