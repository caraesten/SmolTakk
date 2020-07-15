package controllers

import controllers.LoginController.Companion.PARAM_ERRORS
import io.ktor.application.ApplicationCall
import io.ktor.request.ContentTransformationException
import io.ktor.request.receiveParameters
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import repositories.UserRepository
import views.LoginRedirectView
import views.LoginView
import views.RedirectView
import views.View
import web.Router.Companion.ROOM_URL
import web.SiteSession


private const val ERROR_BAD_LOGIN = "badlogin"
private const val PARAM_EMAIL = "email"
private const val PARAM_PASSWORD = "password"

interface LoginController {
    suspend fun getLoginPage(call: ApplicationCall): View
    suspend fun performLogin(call: ApplicationCall): View

    companion object {
        const val PARAM_ERRORS = "errors"
    }
}

class LoginControllerImpl(val userRepository: UserRepository) : LoginController {
    override suspend fun getLoginPage(call: ApplicationCall): View {
        val params = try {
            call.receiveParameters()
        } catch (e: ContentTransformationException) { null }
        val errorList = params?.get(PARAM_ERRORS)?.split(",") ?: emptyList()
        return LoginView(call, errorList)
    }

    override suspend fun performLogin(call: ApplicationCall): View {
        val params = call.receiveParameters()
        val email = params[PARAM_EMAIL]
        val password = params[PARAM_PASSWORD]
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return LoginRedirectView(call, errors = listOf(ERROR_BAD_LOGIN))
        }
        return userRepository.loginUser(email, password)?.let {
            call.sessions.set(SiteSession(authToken = it.authToken))
            RedirectView(call, ROOM_URL)
        } ?: LoginRedirectView(call, errors = listOf(ERROR_BAD_LOGIN))
    }
}
