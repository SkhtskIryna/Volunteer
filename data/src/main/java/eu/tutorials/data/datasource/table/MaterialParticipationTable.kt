package eu.tutorials.data.datasource.table

import eu.tutorials.domain.model.MaterialParticipationStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MaterialParticipationTable: IntIdTable("material_participation") {
    val status = enumerationByName("status", 15, MaterialParticipationStatus::class)
    val id_volunteer = reference("id_volunteer", UserTable)
    val id_material = reference("id_material", MaterialHelpTable)
    val joined_at = datetime("joined_at").clientDefault { LocalDateTime.now() }
    val delivered_at = datetime("delivered_at").nullable()
}