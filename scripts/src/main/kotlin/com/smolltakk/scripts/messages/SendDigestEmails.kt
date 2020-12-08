package com.smolltakk.scripts.messages

class SendDigestEmails {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val testEmailAddress = args.find { it.startsWith("testUsername=") }?.replace("testUsername=", "") ?: ""
            val siteUrl = args.find { it.startsWith("siteUrl=") }?.replace("siteUrl=", "") ?: ""
            if (!testEmailAddress.isEmpty()) {
                com.smoltakk.emails.SendDigestEmailsTask.sendTestDigestEmail("localhost", testEmailAddress, siteUrl)
            } else {
                com.smoltakk.emails.SendDigestEmailsTask.sendAllDigestEmails("localhost", siteUrl)
            }
        }
    }

}