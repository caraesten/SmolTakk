package com.smoltakk.application.controllers

import com.smoltakk.models.Urls.getRoomUrl
import com.smoltakk.application.controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.UserRepository
import com.smoltakk.application.di.ApplicationSingleton
import com.smoltakk.application.views.Http404View
import com.smoltakk.application.views.LoginRedirectView
import com.smoltakk.application.views.RedirectView
import com.smoltakk.application.views.View
import javax.inject.Inject

interface HomePageController : WithAuth, Controller {
    fun getHomePage(call: ApplicationCall): View
}

@ApplicationSingleton
class HomePageControllerImpl @Inject constructor(override val userRepository: UserRepository, val messagesRepository: MessagesRepository) : HomePageController {
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
