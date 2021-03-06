package com.smoltakk.application.views

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode

class Http403View(override val call: ApplicationCall) : ErrorView(call) {
    override val statusCode = HttpStatusCode.Unauthorized
    override val templatePath = "403.html"
}
