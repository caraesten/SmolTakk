package com.smoltakk.emails

import com.smoltakk.models.Room
import com.smoltakk.models.Topic
import com.smoltakk.models.Urls
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import java.time.LocalDateTime
import java.time.Period

class DigestEmailView(private val topics: List<Topic>, private val createdAt: LocalDateTime, private val siteUrl: String) : EmailView {
    override val subject = "Yr daily TALK digest!"

    private val willExpire = (createdAt + Period.ofDays(Room.ROOM_TTL_DAYS))


    override fun renderHtml() = buildString {
        appendln("<!DOCTYPE html>")
        appendHTML().html {
            body {
                h1 {
                    text("TALK digest")
                }
                h3 {
                    text("These discussion will expire on:")
                }
                h3 {
                    text("${willExpire.month} ${willExpire.dayOfMonth} at ${willExpire.hour}:${willExpire.minute} GMT")
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
                            a(href="http://$siteUrl${Urls.getReviveUrl(topic.id.toString())}") {
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