package com.smoltakk.application.views

import io.ktor.application.ApplicationCall

interface View {
    val call: ApplicationCall
    suspend fun render()
}