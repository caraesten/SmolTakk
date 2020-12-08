package com.smoltakk.emails

import com.smoltakk.models.User
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

private const val FROM_FIELD = "no_reply@smoltakk.com"

class EmailSender(private val smtpSession: Session) {
    fun sendEmailToUsers(view: EmailView, users: List<User>) {
        val message = MimeMessage(smtpSession).apply {
            addHeader("Content-type", "text/HTML; charset=UTF-8")
            addHeader("format", "flowed")
            addHeader("Content-Transfer-Encoding", "8bit")
            setFrom(InternetAddress(FROM_FIELD))
            setReplyTo(InternetAddress.parse(FROM_FIELD, false))
            setSubject(view.subject)
            val multipart = MimeMultipart("alternative").apply {
                addBodyPart(MimeBodyPart().apply { setText(view.renderHtml(), "utf-8", "html") })
                addBodyPart(MimeBodyPart().apply { setText(view.renderText(), "utf-8", "text")})
            }
            setContent(multipart)
            setSentDate(Date())
            setRecipients(Message.RecipientType.BCC, InternetAddress.parse(users.map{ it.email}.joinToString(","), false)) }
        Transport.send(message)
    }
}