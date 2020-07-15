package controllers.mixins

import constants.AppConstants
import controllers.Controller
import io.ktor.application.ApplicationCall
import io.ktor.sessions.sessions
import io.ktor.sessions.get
import models.User
import repositories.UserRepository
import views.LoginRedirectView
import views.View
import web.SiteSession
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
