package com.smoltakk.application.views

import com.smoltakk.models.Urls.LOGIN_URL
import com.smoltakk.application.controllers.LoginController
import io.ktor.application.ApplicationCall

class LoginRedirectView(aCall: ApplicationCall, errors: List<String> = emptyList()) :
    RedirectView(aCall, if (errors.isNullOrEmpty()) LOGIN_URL else getLoginUrlWithErrors(errors)) {

    companion object {
        fun getLoginUrlWithErrors(errors: List<String>): String {
            return "$LOGIN_URL?${LoginController.PARAM_ERRORS}=${errors.joinToString(",")}"
        }
    }
}