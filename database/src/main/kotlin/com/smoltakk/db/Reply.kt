package com.smoltakk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.datetime

object Reply : IntIdTable(), Message {
    val parent: Column<Int> = (integer("parent").references(Topic.id))
    override val body = text("body")
    override val author = (integer("author_id").references(User.id))
    override val posted = datetime("posted")
}
