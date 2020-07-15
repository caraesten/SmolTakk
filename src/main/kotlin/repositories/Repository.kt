package repositories

import org.jetbrains.exposed.sql.transactions.transaction

interface Repository {
    fun <T> withTransaction(block: () -> T): T {
        return transaction {
            block()
        }
    }
}