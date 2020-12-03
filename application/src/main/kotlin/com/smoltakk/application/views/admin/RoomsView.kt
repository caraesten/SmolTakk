package com.smoltakk.application.views.admin

import io.ktor.application.ApplicationCall
import com.smoltakk.models.Room
import com.smoltakk.application.views.MustacheView
import com.smoltakk.application.views.admin.viewmodels.Rooms

class RoomsView(call: ApplicationCall, rooms: List<Room>) : MustacheView<Rooms>(call) {
    override val templatePath = "admin_rooms.html"
    override val templateData = Rooms(rooms)
}
