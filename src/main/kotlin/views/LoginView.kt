package views

import io.ktor.application.ApplicationCall
import views.viewmodels.Login

class LoginView(call: ApplicationCall, errors: List<String>) : MustacheView<Login>(call) {
    override val templatePath = "path to login view"
    override val templateData = Login(errors = errors)
}