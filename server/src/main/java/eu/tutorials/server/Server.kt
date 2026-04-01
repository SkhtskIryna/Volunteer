package eu.tutorials.server

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = ServerFactory.createServer(port)
    server.start()
}