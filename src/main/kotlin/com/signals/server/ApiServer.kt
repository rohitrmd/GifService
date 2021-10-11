package com.signals.server

import com.google.inject.Guice
import com.google.inject.Inject
import com.signals.guice.HttpClientModule
import com.signals.guice.ServerModule
import org.eclipse.jetty.server.Server

class ApiServer @Inject constructor(private val server: Server) {
    fun run() {
        server.start()
        server.dumpStdErr()
        server.join()
    }
}

object SignalsGifServer {
    @JvmStatic
    fun main(args: Array<String>) {
        val modules = listOf(ServerModule())
        val injector = Guice.createInjector(listOf(ServerModule(),
            HttpClientModule()))
        injector.getInstance(ApiServer::class.java).run()
    }
}