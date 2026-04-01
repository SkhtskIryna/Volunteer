package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object BinTable: IntIdTable("bin") {
    val id_recipient = reference("id_recipient", UserTable)
    val added_at = datetime("added_at").clientDefault { LocalDateTime.now() }
}