package com.smoltakk.application.views

import io.ktor.application.ApplicationCall
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond

abstract class MustacheView<T>(override val call: ApplicationCall) : View {
    abstract val templatePath: String
    abstract val templateData: T?

    override suspend fun render() {
        call.respond(MustacheContent(templatePath, templateData))
    }
}
