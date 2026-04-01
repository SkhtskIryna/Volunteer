package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object CardTable: IntIdTable("card") {
    val number = varchar("number", 50)
    val validity_period = varchar("validity_period", 10)
    val id_recipient = reference("id_recipient", UserTable)
    val updated_at = datetime("updated_at").nullable()
}