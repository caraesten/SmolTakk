package com.smoltakk.application.controllers

import com.smoltakk.models.Urls.ROOM_URL
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.controllers.LoginController.Companion.PARAM_ERRORS
import com.smoltakk.application.di.ApplicationSingleton
import io.ktor.application.ApplicationCall
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import com.smoltakk.application.views.LoginRedirectView
import com.smoltakk.application.views.LoginView
import com.smoltakk.application.views.RedirectView
import com.smoltakk.application.views.View
import com.smoltakk.application.web.SiteSession
import javax.inject.Inject


private const val ERROR_BAD_LOGIN = "badlogin"
private const val PARAM_EMAIL = "email"
private const val PARAM_PASSWORD = "password"

interface LoginController : Controller {
    suspend fun getLoginPage(call: ApplicationCall): View
    suspend fun performLogin(call: ApplicationCall): View
    suspend fun performLogout(call: ApplicationCall): View

    companion object {
        const val PARAM_ERRORS = "errors"
    }
}

@ApplicationSingleton
class LoginControllerImpl @Inject constructor(val userRepository: UserRepository) : LoginController {
    override suspend fun getLoginPage(call: ApplicationCall): View {
        val params = call.getParametersOrNull()
        val errorList = params?.get(PARAM_ERRORS)?.split(",") ?: emptyList()
        return LoginView(call, errorList)
    }

    override suspend fun performLogin(call: ApplicationCall): View {
        val params = call.getParametersOrNull()
        val email = params?.get(PARAM_EMAIL)
        val password = params?.get(PARAM_PASSWORD)
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return LoginRedirectView(call, errors = listOf(ERROR_BAD_LOGIN))
        }
        return withTransaction(userRepository) {
            userRepository.loginUser(email, password)?.let {
                call.sessions.set(SiteSession(authToken = it.authToken))
                RedirectView(call, ROOM_URL)
            } ?: LoginRedirectView(call, errors = listOf(ERROR_BAD_LOGIN))
        }
    }

    override suspend fun performLogout(call: ApplicationCall): View {
        call.sessions.set(SiteSession(authToken = ""))
        return LoginRedirectView(call)
    }
}
