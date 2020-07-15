package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import repositories.MessagesRepository
import repositories.UserRepository
import views.Http404View
import views.LoginRedirectView
import views.RedirectView
import views.View
import web.Router.Companion.getRoomUrl

interface HomePageController : WithAuth {
    fun getHomePage(call: ApplicationCall): View
}

class HomePageControllerImpl(override val userRepository: UserRepository, val messagesRepository: MessagesRepository) : HomePageController {
    override fun getHomePage(call: ApplicationCall): View {
        return getLoggedInUser(call)?.let {
            messagesRepository.getActiveRoom()?.let { room ->
                RedirectView(call, getRoomUrl(room.id))
            } ?: Http404View(call)
        } ?: LoginRedirectView(call)
    }
}
