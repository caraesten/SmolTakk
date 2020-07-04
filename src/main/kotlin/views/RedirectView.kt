package views

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect

abstract class RedirectView(override val call: ApplicationCall) : View {
    abstract val path: String

    override suspend fun render() {
        call.respondRedirect(path)
    }
}
