package com.smoltakk.application.controllers

import com.smoltakk.models.Urls.getProfileUrl
import com.smoltakk.models.User
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.controllers.mixins.WithAuth
import com.smoltakk.application.di.ApplicationSingleton
import io.ktor.application.ApplicationCall
import com.smoltakk.application.views.Http403View
import com.smoltakk.application.views.Http404View
import com.smoltakk.application.views.RedirectView
import com.smoltakk.application.views.View
import com.smoltakk.application.views.admin.RoomsView
import com.smoltakk.application.views.admin.UsersView
import javax.inject.Inject

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

@ApplicationSingleton
class AdminControllerImpl @Inject constructor(override val userRepository: UserRepository, val messagesRepository: MessagesRepository) : AdminController {
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
                val userStatus = withTransaction(userRepository) { userRepository.createUser(email, username, password) }
                if (userStatus is UserRepository.UserUpdateStatus.Success) {
                    RedirectView(call, getProfileUrl(userStatus.user.username))
                } else {
                    RedirectView(call, "")
                }
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