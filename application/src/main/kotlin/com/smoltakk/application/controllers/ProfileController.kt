package com.smoltakk.application.controllers

import com.smoltakk.models.Urls.getProfileUrl
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.controllers.mixins.WithAuth
import com.smoltakk.application.di.ApplicationSingleton
import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import com.smoltakk.application.views.*
import com.smoltakk.application.web.SiteSession
import javax.inject.Inject


private const val PARAM_USERNAME = "username"
private const val PARAM_PASSWORD = "password"
private const val PARAM_EMAIL = "email"

interface ProfileController : WithAuth, Controller {
    suspend fun getProfile(call: ApplicationCall, username: String?): View
    suspend fun updateProfile(call: ApplicationCall, username: String?): View
}

@ApplicationSingleton
class ProfileControllerImpl @Inject constructor(override val userRepository: UserRepository) : ProfileController {
    override suspend fun getProfile(call: ApplicationCall, username: String?): View {
        if (username.isNullOrEmpty()) return Http404View(call)
        return withAuth(call) {
            withTransaction(userRepository) {
                userRepository.findUserByUsername(username)?.let { user ->
                ProfileView(call, user)
                } ?: Http404View(call)
            }
        }
    }

    override suspend fun updateProfile(call: ApplicationCall, username: String?): View {
        if (username.isNullOrEmpty()) return Http404View(call)
        val params = call.receiveParameters()
        return withAuth(call) { loggedInUser ->
            withTransaction(userRepository) {
                userRepository.findUserByUsername(username)?.let { user ->
                    if (user != loggedInUser) {
                        Http403View(call)
                    } else {
                        val newUsername = if (params[PARAM_USERNAME].equals(loggedInUser.username)) null else params[PARAM_USERNAME]
                        val newEmail = if (params[PARAM_EMAIL].equals(loggedInUser.username)) null else params[PARAM_EMAIL]
                        val result = userRepository.updateUserProfile(
                            loggedInUser,
                            newUsername,
                            params[PARAM_PASSWORD],
                            newEmail
                        )
                        val redirUser = if (result is UserRepository.UserUpdateStatus.Success) {
                            call.sessions.set(SiteSession(authToken = result.user.authToken))
                            result.user
                        } else {
                            user
                        }
                        RedirectView(call, getProfileUrl(redirUser.username))
                    }
                } ?: Http404View(call)
            }
        }
    }
}
