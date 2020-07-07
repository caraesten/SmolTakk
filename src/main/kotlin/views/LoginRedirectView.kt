package views

import io.ktor.application.ApplicationCall
import web.Router.Companion.LOGIN_URL

class LoginRedirectView(aCall: ApplicationCall) : RedirectView(aCall, LOGIN_URL)