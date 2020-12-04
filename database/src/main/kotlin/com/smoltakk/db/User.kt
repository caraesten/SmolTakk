package com.smoltakk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime

object User: IntIdTable(){
    val email: Column<String> = varchar("email", 50).uniqueIndex()
    val username: Column<String> = varchar("username", 20).uniqueIndex()
    val hashedPassword: Column<String> = varchar("hashed_password", 200)
    val salt: Column<String> = varchar("salt", 200)
    val authToken: Column<String> = varchar("auth_token", 200)
    val tokenIssued: Column<LocalDateTime> = datetime("token_issued")
    val titleTextColor: Column<String> = varchar("title_text_color", 16).default("fff")
    val titleBackgroundColor: Column<String> = varchar("title_background_color", 16).default("000")
}