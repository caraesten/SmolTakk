package views

import io.ktor.application.ApplicationCall
import views.viewmodels.Login
import web.Router.Companion.LOGIN_URL

class LoginView(call: ApplicationCall, errors: List<String>) : MustacheView<Login>(call) {
    override val templatePath = "login.html"
    override val templateData = Login(errors = errors)
}