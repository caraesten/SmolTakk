package views.admin

import io.ktor.application.ApplicationCall
import models.Room
import views.MustacheView
import views.admin.viewmodels.Rooms

class RoomsView(call: ApplicationCall, rooms: List<Room>) : MustacheView<Rooms>(call) {
    override val templatePath = "path to room admin"
    override val templateData = Rooms(rooms)
}
