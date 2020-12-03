package com.smolltakk.scripts

import com.smolltakk.scripts.db.CreateDb
import com.smolltakk.scripts.messages.CreateRoom
import com.smolltakk.scripts.messages.SendDigestEmails
import com.smolltakk.scripts.users.InitializeAdminUser
import com.smolltakk.scripts.users.UpdateUser

class ScriptsMain {

    companion object {
        val tools = mapOf(
            "initializeAdmin" to { args:Array<String> -> InitializeAdminUser.main(args) },
            "updateUser" to { args:Array<String> -> UpdateUser.main(args) },
            "sendDigestEmails" to { args:Array<String> -> SendDigestEmails.main(args) },
            "createDb" to { args:Array<String> -> CreateDb.main(args) },
            "createRoom" to { args:Array<String> -> CreateRoom.main(args) }
        )
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                outputHelp()
                return
            }
            val slicedArgs = args.drop(1).toTypedArray()
            val functionToRun = tools[args[0]]
            functionToRun?.invoke(slicedArgs)
            if (functionToRun == null) {
                outputHelp()
            }
        }

        private fun outputHelp() {
            println("Commands: ${tools.keys.joinToString()}")
        }
    }
}