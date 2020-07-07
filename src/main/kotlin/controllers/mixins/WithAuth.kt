package controllers.mixins

import constants.AppConstants
import io.ktor.application.ApplicationCall
import models.User
import repositories.UserRepository
import views.LoginRedirectView
import views.View

interface WithAuth {
    val userRepository: UserRepository

    private fun getLoggedInUser(call: ApplicationCall): User? {
        return call.request.headers[AppConstants.AUTH_TOKEN_HEADER]?.let {
            userRepository.findUserByAuthToken(it)
        }
    }

    suspend fun withAuth(call: ApplicationCall, action: suspend (User) -> View): View {
        return getLoggedInUser(call)?.let {
            action(it)
        } ?: LoginRedirectView(call)
    }
}
