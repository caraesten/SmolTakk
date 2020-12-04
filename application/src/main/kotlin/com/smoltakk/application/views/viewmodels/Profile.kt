package com.smoltakk.application.views.viewmodels

import com.smoltakk.models.Urls

data class Profile (
    val email: String,
    val username: String,
    val titleTextColor: String,
    val titleBackgroundColor: String
) {
    val profileUrl = Urls.PROFILE_URL + username
    val logoutUrl = Urls.LOGOUT_URL
}