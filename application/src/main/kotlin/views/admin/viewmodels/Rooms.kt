package views.admin.viewmodels

import com.smoltakk.models.Room
import com.smoltakk.models.Urls.ADMIN_NEW_ROOM_PATH

data class Rooms (val rooms: List<Room>) {
    val formAction = ADMIN_NEW_ROOM_PATH
}