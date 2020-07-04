package controllers

import controllers.mixins.WithAuth
import io.ktor.application.ApplicationCall
import repositories.UserRepository
import views.View

interface AdminController : WithAuth {
    fun showUserAdminPage(applicationCall: ApplicationCall): View
    fun showRoomAdminPage(applicationCall: ApplicationCall): View
    fun createUser(applicationCall: ApplicationCall): View
    fun createRoom(applicationCall: ApplicationCall): View
}

class AdminControllerImpl(override val userRepository: UserRepository) : AdminController {
    override fun showUserAdminPage(applicationCall: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun showRoomAdminPage(applicationCall: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun createUser(applicationCall: ApplicationCall): View {
        TODO("Not yet implemented")
    }

    override fun createRoom(applicationCall: ApplicationCall): View {
        TODO("Not yet implemented")
    }

}