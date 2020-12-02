package com.smolltakk.scripts

import com.smolltakk.scripts.db.CreateDb
import com.smolltakk.scripts.messages.SendDigestEmails
import com.smolltakk.scripts.users.InitializeAdminUser
import com.smolltakk.scripts.users.UpdateUser

class ScriptsMain {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val slicedArgs = args.drop(0).toTypedArray()
            when (args.getOrElse(0) { "" }) {
                "initializeAdmin" -> {
                    InitializeAdminUser.main(slicedArgs)
                }
                "updateUser" -> {
                    UpdateUser.main(slicedArgs)
                }
                "sendDigestEmails" -> {
                    SendDigestEmails.main(slicedArgs)
                }
                "createDb" -> {
                    CreateDb.main(slicedArgs)
                }
                else -> {
                    System.out.println("Commands: initializeAdmin, updateUser, sendDigestEmails, createDb")
                }
            }
        }
    }
}