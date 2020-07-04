package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import repositories.UserRepository
import views.View

interface ProfileController : WithAuth {
    fun getProfile(call: ApplicationCall): View
    fun updateProfile(call: ApplicationCall): View
}

class ProfileControllerImpl(override val userRepository: UserRepository) : ProfileController {
    override fun getProfile(call: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun updateProfile(call: ApplicationCall): View {
        TODO("Not yet implemented")
    }
}
