package controllers

import io.ktor.application.ApplicationCall
import views.View

interface LoginController {
    fun getLoginPage(call: ApplicationCall): View
    fun performLogin(call: ApplicationCall): View
}

class LoginControllerImpl : LoginController {
    override fun getLoginPage(call: ApplicationCall): View {

    }

    override fun performLogin(call: ApplicationCall): View {

    }
}
