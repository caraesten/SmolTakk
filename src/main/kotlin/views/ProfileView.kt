package views

import io.ktor.application.ApplicationCall
import models.User
import views.viewmodels.Profile

class ProfileView(override val call: ApplicationCall, user: User) : MustacheView<Profile>(call) {
    override val templatePath = "path to profile template"
    override val templateData = Profile(email = user.email, username = user.username)
}