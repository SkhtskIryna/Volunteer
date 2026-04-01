package eu.tutorials.data.datasource.config

import com.typesafe.config.ConfigFactory
import eu.tutorials.data.datasource.table.BinTable
import eu.tutorials.data.datasource.table.CardTable
import eu.tutorials.data.datasource.table.DonationTable
import eu.tutorials.data.datasource.table.FinancialHelpTable
import eu.tutorials.data.datasource.table.HelpTable
import eu.tutorials.data.datasource.table.HistoryTable
import eu.tutorials.data.datasource.table.MaterialHelpTable
import eu.tutorials.data.datasource.table.MaterialParticipationTable
import eu.tutorials.data.datasource.table.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = ConfigFactory.load().getConfig("database")

        Database.connect(
            url = config.getString("url"),
            driver = config.getString("driver"),
            user = config.getString("user"),
            password = config.getString("password")
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UserTable,
                BinTable,
                CardTable,
                HelpTable,
                HistoryTable,
                FinancialHelpTable,
                DonationTable,
                MaterialHelpTable,
                MaterialParticipationTable,
            )
        }
    }
}