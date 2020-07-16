package controllers

import com.smoltakk.models.User
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import views.Http403View
import views.RedirectView
import views.View
import views.admin.RoomsView
import views.admin.UsersView
import web.Router.Companion.getProfileUrl

private const val CARAS_EMAIL = "dondeesten@gmail.com" // lol I'm lazy sorry
private const val PARAM_USERNAME = "username"
private const val PARAM_EMAIL = "email"
private const val PARAM_PASSWORD = "password"

interface AdminController : WithAuth, Controller {
    suspend fun showUserAdminPage(call: ApplicationCall): View
    suspend fun showRoomAdminPage(call: ApplicationCall): View
    suspend fun createUser(call: ApplicationCall): View
    suspend fun createRoom(call: ApplicationCall): View
}

class AdminControllerImpl(override val userRepository: UserRepository, val messagesRepository: MessagesRepository) : AdminController {
    override suspend fun showUserAdminPage(call: ApplicationCall): View {
        return withAdminAuth(call) {
            withTransaction(userRepository) {
                val users = userRepository.getAllUsers()
                UsersView(call, users)
            }
        }
    }

    override suspend fun showRoomAdminPage(call: ApplicationCall): View {
        return withAdminAuth(call) {
            withTransaction(messagesRepository) {
                val rooms = messagesRepository.getAllRooms(true)
                RoomsView(call, rooms)
            }
        }
    }

    override suspend fun createUser(call: ApplicationCall): View {
        return withAdminAuth(call) {
            val params = call.getParametersOrNull()
            val email = params?.get(PARAM_EMAIL)
            val username = params?.get(PARAM_USERNAME)
            val password = params?.get(PARAM_PASSWORD)
            if (email.isNullOrEmpty() || username.isNullOrEmpty() || password.isNullOrEmpty()) {
                RedirectView(call, "")
            } else {
                val user = withTransaction(userRepository) { userRepository.createUser(email, username, password) }
                RedirectView(call, if (user != null) getProfileUrl(user.username) else "")
            }
        }
    }

    override suspend fun createRoom(call: ApplicationCall): View {
        return withAdminAuth(call) {
            withTransaction(messagesRepository) {
                messagesRepository.createRoom()
            }
            RedirectView(call, "")
        }
    }

    private suspend fun withAdminAuth(call: ApplicationCall, action: suspend (User) -> View): View {
        return withAuth(call) {
            if (it.email != CARAS_EMAIL) {
                Http403View(call)
            } else {
                action(it)
            }
        }
    }
}