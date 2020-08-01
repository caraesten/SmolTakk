package com.smoltakk.repositories.tools

import com.smoltakk.db.DatabaseFactory
import com.smoltakk.repositories.MessagesRepository
import com.smoltakk.repositories.MessagesRepositoryImpl

open class MessagesTools {
    val dbUrl = System.getenv("ST_DB_URL")!!
    val dbUser = System.getenv("ST_DB_USER")!!
    val dbPass = System.getenv("ST_DB_PASSWORD")!!

    fun getMessagesRepo(): MessagesRepository {
        val database = DatabaseFactory(dbUrl, dbUser, dbPass).init()
        return MessagesRepositoryImpl(database, UserTools().getUserRepo())
    }
}