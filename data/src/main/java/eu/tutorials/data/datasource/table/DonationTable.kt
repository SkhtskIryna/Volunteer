package eu.tutorials.data.datasource.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object DonationTable: IntIdTable("donation") {
    val sum = double("sum")
    val id_volunteer = reference("id_volunteer", UserTable)
    val id_financial = reference("id_financial", FinancialHelpTable)
    val donation_at = datetime("donation_at").clientDefault { LocalDateTime.now() }
}