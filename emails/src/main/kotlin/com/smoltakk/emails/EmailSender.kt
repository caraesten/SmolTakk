package com.smoltakk.emails

import com.smoltakk.models.User
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
            setText(view.renderHtml(), "UTF-8")
            setSentDate(Date())
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(users.map{ it.email}.joinToString(","), false)) }
        Transport.send(message)
    }
}