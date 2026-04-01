package eu.tutorials.server

object ServerFactory {
    fun createServer(port: Int) = KtorServer(port)
}