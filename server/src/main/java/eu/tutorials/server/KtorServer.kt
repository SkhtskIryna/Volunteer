package eu.tutorials.server

import com.typesafe.config.ConfigFactory
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import org.jetbrains.exposed.sql.Database

class KtorServer(private val port: Int = 8080) {
    private var engine: ApplicationEngine? = null

    fun start() {
        if (engine == null) {
            val config = ConfigFactory.load()
            val dbConfig = config.getConfig("database")

            Database.connect(
                url = dbConfig.getString("url"),
                driver = dbConfig.getString("driver"),
                user = dbConfig.getString("user"),
                password = dbConfig.getString("password")
            )

            engine = embeddedServer(CIO, host = "0.0.0.0", port = port) {
                module()
            }
            engine?.start(wait = true)
        }
    }
}