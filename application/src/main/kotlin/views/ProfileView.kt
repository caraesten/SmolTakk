package views

import io.ktor.application.ApplicationCall
import com.smoltakk.models.User
import views.viewmodels.Profile

class ProfileView(override val call: ApplicationCall, user: User) : MustacheView<Profile>(call) {
    override val templatePath = "profile.html"
    override val templateData = Profile(email = user.email, username = user.username)
}