package com.smoltakk.repositories.tools

import com.smoltakk.db.DatabaseFactory
import com.smoltakk.repositories.UserRepositoryImpl

object InitializeAdminUser : UserTools() {
    // Eventually this'll share the application config, but it does not now!
    private val adminUsername = System.getenv("ST_ADMIN_USER")!!
    private val adminEmail = System.getenv("ST_ADMIN_EMAIL")!!
    private val adminPass = System.getenv("ST_ADMIN_PASS")!!

    fun run() {
        val userRepository = getUserRepo()
        userRepository.withTransaction {
            userRepository.createUser(
                adminEmail,
                adminUsername,
                adminPass
            )
        }
    }
}