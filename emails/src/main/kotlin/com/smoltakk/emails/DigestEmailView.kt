package com.smoltakk.emails

import com.smoltakk.models.Topic
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML

class DigestEmailView(private val topics: List<Topic>) : EmailView {
    override val subject = "Yr daily TALK digest!"

    override fun renderHtml() = buildString {
        appendln("<!DOCTYPE html>")
        appendHTML().html {
            body {
                h1 {
                    text("TALK email")
                }
            }
        }
    }

    override fun renderText(): String {
        TODO("Not yet implemented")
    }
}