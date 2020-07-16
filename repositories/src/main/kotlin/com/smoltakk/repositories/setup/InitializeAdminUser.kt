package com.smoltakk.repositories.setup

import com.smoltakk.db.DatabaseFactory
import com.smoltakk.repositories.UserRepositoryImpl

object InitializeAdminUser {
    // Eventually this'll share the application config, but it does not now!
    private val adminUsername = System.getenv("ST_ADMIN_USER")!!
    private val adminEmail = System.getenv("ST_ADMIN_EMAIL")!!
    private val adminPass = System.getenv("ST_ADMIN_PASS")!!
    private val tokenSecret = System.getenv("ST_TOKEN_SECRET")!!
    private val saltSecret = System.getenv("ST_SALT_SECRET")!!
    private val dbUrl = System.getenv("ST_DB_URL")!!
    private val dbUser = System.getenv("ST_DB_USER")!!
    private val dbPass = System.getenv("ST_DB_PASSWORD")!!

    fun setup() {
        val database = DatabaseFactory(dbUrl, dbUser, dbPass).init()
        val userRepository = UserRepositoryImpl(database, saltSecret, tokenSecret)
        userRepository.withTransaction {
            userRepository.createUser(
                adminEmail,
                adminUsername,
                adminPass
            )
        }
    }
}