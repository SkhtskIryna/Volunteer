package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date

object FinancialHelpTable: IdTable<Int>("financial_help") {
    override val id: Column<EntityID<Int>> = reference("id", HelpTable)
    override val primaryKey = PrimaryKey(id)

    val from = date("from")
    val to = date("to")
    val planned_amount = double("planned_amount")
    val collected = double("collected").nullable()
}