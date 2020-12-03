package com.smoltakk.application.views.admin

import com.smoltakk.models.User
import io.ktor.application.ApplicationCall
import com.smoltakk.application.views.MustacheView
import com.smoltakk.application.views.admin.viewmodels.Users

class UsersView(call: ApplicationCall, users: List<User>) : MustacheView<Users>(call) {
    override val templatePath = "admin_users.html"
    override val templateData = Users(users)
}
