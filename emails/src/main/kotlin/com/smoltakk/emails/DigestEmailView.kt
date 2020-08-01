package com.smoltakk.emails

import com.smoltakk.models.Topic

class DigestEmailView(private val topics: List<Topic>) : EmailView {
    override val subject = "Yr daily TALK digest!"

    override fun renderHtml(): String {
        TODO("Not yet implemented")
    }

    override fun renderText(): String {
        TODO("Not yet implemented")
    }
}