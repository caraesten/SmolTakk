package com.smoltakk.emails

interface EmailView {
    val subject: String
    fun renderHtml(): String
    fun renderText(): String
}