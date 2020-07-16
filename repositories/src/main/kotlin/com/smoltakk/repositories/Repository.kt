package com.smoltakk.repositories

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

interface Repository {
    val database: Database
    fun <T> withTransaction(block: () -> T): T {
        return transaction(database) {
            block()
        }
    }
}