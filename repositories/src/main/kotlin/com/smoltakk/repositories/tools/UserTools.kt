package com.smoltakk.repositories.tools

import com.smoltakk.db.DatabaseFactory
import com.smoltakk.repositories.UserRepository
import com.smoltakk.repositories.UserRepositoryImpl

open class UserTools {
    val tokenSecret = System.getenv("ST_TOKEN_SECRET")!!
    val saltSecret = System.getenv("ST_SALT_SECRET")!!
    val dbUrl = System.getenv("ST_DB_URL")!!
    val dbUser = System.getenv("ST_DB_USER")!!
    val dbPass = System.getenv("ST_DB_PASSWORD")!!

    fun getUserRepo(): UserRepository {
        val database = DatabaseFactory(dbUrl, dbUser, dbPass).init()
        return UserRepositoryImpl(database, saltSecret, tokenSecret)
    }
}