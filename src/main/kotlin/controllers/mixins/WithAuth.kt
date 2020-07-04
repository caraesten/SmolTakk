package controllers.mixins

import constants.AppConstants
import io.ktor.application.ApplicationCall
import models.User
import repositories.UserRepository

interface WithAuth {
    val userRepository: UserRepository

    fun getLoggedInUser(call: ApplicationCall): User? {
        return call.request.headers[AppConstants.AUTH_TOKEN_HEADER]?.let {
            userRepository.findUserByAuthToken(it)
        }
    }
}