package com.smoltakk.emails

import com.smoltakk.models.User
import com.smoltakk.repositories.UserRepository
import com.smoltakk.repositories.UserRepositoryImpl
import com.smoltakk.repositories.tools.MessagesTools
import com.smoltakk.repositories.tools.UserTools
import javax.mail.Session

object SendDigestEmailsTask {
    val userRepository = UserTools().getUserRepo()
    val messagesRepository = MessagesTools().getMessagesRepo()

    fun sendTestDigestEmail(smtpHost: String, sendTo: String /* username */) {
        userRepository.withTransaction {
            val users = listOfNotNull(userRepository.findUserByUsername(sendTo))
            sendEmailTo(users, smtpHost)
        }
    }

    fun sendAllDigestEmails(smtpHost: String) {
        userRepository.withTransaction {
            sendEmailTo(userRepository.getAllUsers(), smtpHost)
        }
    }

    private fun sendEmailTo(users: List<User>, smtpHost: String) {
        val session = Session.getInstance(System.getProperties().apply { put("mail.smtp.host", smtpHost)})
        val emailSender = EmailSender(session)

        val room = messagesRepository.getActiveRoom()
        room?.let {
            emailSender.sendEmailToUsers(DigestEmailView(it.topics), users)
        }
    }
}