package views

import controllers.LoginController
import io.ktor.application.ApplicationCall
import web.Router.Companion.LOGIN_URL

class LoginRedirectView(aCall: ApplicationCall, errors: List<String> = emptyList()) :
    RedirectView(aCall, if (errors.isNullOrEmpty()) LOGIN_URL else getLoginUrlWithErrors(errors)) {

    companion object {
        fun getLoginUrlWithErrors(errors: List<String>): String {
            return "$LOGIN_URL?${LoginController.PARAM_ERRORS}=${errors.joinToString(",")}"
        }
    }
}