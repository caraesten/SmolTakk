package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import views.Http404View
import views.LoginRedirectView
import views.RedirectView
import views.View
import web.Router.Companion.getRoomUrl

interface HomePageController : WithAuth, Controller {
    fun getHomePage(call: ApplicationCall): View
}

class HomePageControllerImpl(override val userRepository: UserRepository, val messagesRepository: MessagesRepository) : HomePageController {
    override fun getHomePage(call: ApplicationCall): View {
        return getLoggedInUser(call)?.let {
            withTransaction(messagesRepository) {
                messagesRepository.getActiveRoom()?.let { room ->
                    RedirectView(call, getRoomUrl(room.id))
                } ?: Http404View(call)
            }
        } ?: LoginRedirectView(call)
    }
}
