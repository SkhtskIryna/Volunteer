package eu.tutorials.data.datasource.table

import eu.tutorials.domain.model.HistoryStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object HistoryTable: IntIdTable("history") {
    val status = enumerationByName("status", 12, HistoryStatus::class)
    val id_admin = reference("id_admin", UserTable).nullable()
    val id_help = reference("id_help", HelpTable)
    val added_at = datetime("added_at").clientDefault { LocalDateTime.now() }
}