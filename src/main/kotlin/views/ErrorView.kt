package views

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond

abstract class ErrorView(override val call: ApplicationCall) : View {
    abstract val statusCode: HttpStatusCode
    abstract val templatePath: String

    override suspend fun render() {
        call.respond(statusCode, MustacheContent(templatePath, null))
    }
}