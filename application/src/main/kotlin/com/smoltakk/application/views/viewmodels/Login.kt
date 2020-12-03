package com.smoltakk.application.views.viewmodels

import com.smoltakk.models.Urls.LOGIN_URL

data class Login(val errors: List<String>) {
    val loginFormAction = LOGIN_URL
}