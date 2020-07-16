package com.smoltakk.db.setup

import com.smoltakk.db.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object InitializeDb {
    // Eventually this'll share the application config, but it does not now!
    private val dbUrl = System.getenv("ST_DB_URL")!!
    private val dbUser = System.getenv("ST_DB_USER")!!
    private val dbPass = System.getenv("ST_DB_PASSWORD")!!
    fun setup() {
        val db = DatabaseFactory(dbUrl, dbUser, dbPass).init()
        transaction(db) {
            SchemaUtils.create(Reply, Room, Topic, User)
            //Do stuff
        }
    }
}