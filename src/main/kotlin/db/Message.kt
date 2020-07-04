package db

import org.jetbrains.exposed.sql.Column
import java.time.LocalDateTime

interface Message {
    val body: Column<String>
    val author: Column<Int>
    val posted: Column<LocalDateTime>
}
