package com.smoltakk.models

data class Configuration(
    val saltSecret: String,
    val tokenSecret: String,
    val dbUrl: String,
    val dbUser: String,
    val dbPassword: String)