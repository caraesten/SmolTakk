package com.smoltakk.application.views

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode

class Http404View(override val call: ApplicationCall) : ErrorView(call) {
    override val statusCode = HttpStatusCode.NotFound
    override val templatePath = "404.html"
}
