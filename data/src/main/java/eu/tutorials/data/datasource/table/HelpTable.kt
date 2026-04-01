package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object HelpTable: IntIdTable("help") {
    val type = varchar("type", 50)
    val title = varchar("title", 225)
    val description = text("description")
    val id_recipient = reference("id_recipient", UserTable)
    val created_at = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updated_at = datetime("updated_at").nullable()
}