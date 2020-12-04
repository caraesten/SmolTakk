package com.smoltakk.models

data class User(
    val email: String,
    val username: String,
    val authToken: String,
    val id: Int,
    val titleTextColor: String,
    val titleBackgroundColor: String
) {

    companion object {
        fun getEmptyUser(): User =
            User("", "", "", -1, "fff", "000")
    }
}
