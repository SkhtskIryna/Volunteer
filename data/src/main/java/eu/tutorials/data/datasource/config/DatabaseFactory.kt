package eu.tutorials.data.datasource.config

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
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:mysql://volunteer-mysql:3306/volunteer?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        val dbUser = System.getenv("DB_USER") ?: "user"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "password"

        println("Connecting to DB: $dbUrl")

        Database.connect(
            url = dbUrl,
            driver = "com.mysql.cj.jdbc.Driver",
            user = dbUser,
            password = dbPassword
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