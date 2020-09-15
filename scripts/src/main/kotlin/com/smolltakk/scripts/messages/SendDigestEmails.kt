package com.smolltakk.scripts.messages

class SendDigestEmails {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val testEmailAddress = args.find { it.startsWith("testUsername=") }?.replace("testUsername=", "") ?: ""
            if (!testEmailAddress.isEmpty()) {
                com.smoltakk.emails.SendDigestEmailsTask.sendTestDigestEmail("localhost", testEmailAddress)
            } else {
                com.smoltakk.emails.SendDigestEmailsTask.sendAllDigestEmails("localhost")
            }
        }
    }

}