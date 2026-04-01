package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object MaterialHelpTable: IdTable<Int>("material_help") {
    override val id: Column<EntityID<Int>> = reference("id", HelpTable)
    override val primaryKey = PrimaryKey(id)

    val category = text("category")
    val region = varchar("region", 60).nullable()
    val area = varchar("area", 60).nullable()
    val city = varchar("city", 60).nullable()
}