package views

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect

open class RedirectView(override val call: ApplicationCall, val path: String) : View {
    override suspend fun render() {
        call.respondRedirect(path)
    }
}
