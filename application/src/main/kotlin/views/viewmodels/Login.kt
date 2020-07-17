package views.viewmodels

import web.Router

data class Login(val errors: List<String>) {
    val loginFormAction = Router.LOGIN_URL
}