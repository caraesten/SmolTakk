package com.smoltakk.application.controllers.mixins

import com.smoltakk.application.controllers.Controller
import io.ktor.application.ApplicationCall
import io.ktor.sessions.sessions
import io.ktor.sessions.get
import com.smoltakk.models.User
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.views.LoginRedirectView
import com.smoltakk.application.views.View
import com.smoltakk.application.web.SiteSession
import java.lang.IllegalStateException

interface WithAuth : Controller {
    val userRepository: UserRepository

    fun getLoggedInUser(call: ApplicationCall): User? {
        return try { call.sessions.get<SiteSession>() } catch (e: IllegalStateException) { null }?.let {
            userRepository.withTransaction {
                userRepository.findUserByAuthToken(it.authToken)
            }
        }
    }

    suspend fun withAuth(call: ApplicationCall, action: suspend (User) -> View): View {
        return getLoggedInUser(call)?.let {
            action(it)
        } ?: LoginRedirectView(call)
    }
}
