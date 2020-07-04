package controllers

import io.ktor.application.ApplicationCall
import views.View

interface HomePageController {
    fun getHomePage(call: ApplicationCall): View
}

class HomePageControllerImpl : HomePageController {
    override fun getHomePage(call: ApplicationCall): View {
        // If this is an authed user, direct to active room, if not, direct to login page
    }
}
