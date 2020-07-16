package com.smoltakk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.datetime

object Topic: IntIdTable(), Message {
    val title: Column<String> = varchar("title", 100)
    val room: Column<Int> = (integer("room_id").references(Room.id))
    override val body = text("body")
    override val author = (integer("author_id").references(User.id))
    override val posted = datetime("posted")
}