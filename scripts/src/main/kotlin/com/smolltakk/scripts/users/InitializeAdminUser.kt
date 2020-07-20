package com.smolltakk.scripts.users

import com.smoltakk.repositories.tools.InitializeAdminUser

class InitializeAdminUser {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            InitializeAdminUser.run()
        }
    }
}