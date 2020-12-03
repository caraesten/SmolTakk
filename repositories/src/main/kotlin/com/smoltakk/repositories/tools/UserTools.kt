package com.smoltakk.repositories.tools

import com.smoltakk.db.di.DaggerDatabaseComponent
import com.smoltakk.repositories.UserRepository
import com.smoltakk.repositories.UserRepositoryImpl

open class UserTools {
    val tokenSecret = System.getenv("ST_TOKEN_SECRET")!!
    val saltSecret = System.getenv("ST_SALT_SECRET")!!
    val dbUrl = System.getenv("ST_DB_URL")!!
    val dbUser = System.getenv("ST_DB_USER")!!
    val dbPass = System.getenv("ST_DB_PASSWORD")!!

    fun getUserRepo(): UserRepository {
        val database = DaggerDatabaseComponent.factory().newDatabaseComponent(dbUrl, dbUser, dbPass).database()
        return UserRepositoryImpl(database, saltSecret, tokenSecret)
    }
}