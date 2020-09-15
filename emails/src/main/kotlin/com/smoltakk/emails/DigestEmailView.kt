package com.smoltakk.emails

import com.smoltakk.models.Topic
import com.smoltakk.models.Urls
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML

class DigestEmailView(private val topics: List<Topic>) : EmailView {
    override val subject = "Yr daily TALK digest!"

    override fun renderHtml() = buildString {
        appendln("<!DOCTYPE html>")
        appendHTML().html {
            body {
                h1 {
                    text("TALK digest")
                }
                div {
                    topics.forEach { topic ->
                        h2 {
                            text(topic.title)
                        }
                        p {
                            text(topic.body)
                        }
                        p {
                            a(href=Urls.getReviveUrl(topic.id.toString())) {
                                text("Is this from a previous discussion? Click here to revive it!")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun renderText(): String {
        return """
            ${topics.map { topic ->
            "${topic.title} - ${topic.body} \n"
        }.joinToString { "\n" }}
        """.trimIndent()
    }
}