package views.admin

import io.ktor.application.ApplicationCall
import models.Room
import models.User
import views.MustacheView
import views.admin.viewmodels.Rooms
import views.admin.viewmodels.Users

class UsersView(call: ApplicationCall, users: List<User>) : MustacheView<Users>(call) {
    override val templatePath = "path to users admin"
    override val templateData = Users(users)
}
