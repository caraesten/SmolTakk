package com.smolltakk.scripts.users

class UpdateUser {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val username = args[0]
            val newUsername = args.find { it.startsWith("username=") }?.replace("username=", "")
            val newEmail = args.find { it.startsWith("email=") }?.replace("email=", "")
            val newPassword = args.find { it.startsWith("password=") }?.replace("password=", "")

            println("Updating / creating user: $username... ")
            newUsername?.let {
                println("New username: $it")
            }
            newEmail?.let {
                println("New email: $it")
            }
            newPassword?.let {
                println("New password: $it")
            }

            com.smoltakk.repositories.tools.UpdateUser.run(username, newUsername, newEmail, newPassword)
        }
    }
}