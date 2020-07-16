package com.smoltakk.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

object Room : IntIdTable() {
    val isActive = bool("is_active")
    val created = datetime("created")
}
