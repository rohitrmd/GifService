package com.signals.guice

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.signals.server.SignalsServlet
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.thread.QueuedThreadPool

class ServerModule: AbstractModule() {

    @Singleton
    @Provides
    fun provideHttpConfiguration(): HttpConfiguration {
        // these values are hard coded but should be injected as guice props
        val httpConfig = HttpConfiguration()
        httpConfig.secureScheme = "https"
        httpConfig.securePort = 8001
        httpConfig.outputBufferSize = 32768
        httpConfig.requestHeaderSize = 8192
        httpConfig.responseHeaderSize = 8192
        httpConfig.sendServerVersion = true
        httpConfig.sendDateHeader = false

        return httpConfig
    }

    @Provides
    @Singleton
    fun provideThreadPool(): QueuedThreadPool {
        val threadPool = QueuedThreadPool()
        threadPool.maxThreads = Runtime.getRuntime().availableProcessors()
        return threadPool
    }

    @Provides
    @Singleton
    fun provideServletContextHandler(signalsServlet: SignalsServlet):ServletContextHandler {
        val context = ServletContextHandler()
        context.addServlet(ServletHolder(signalsServlet), "/signals")
        return context
    }

    @Provides
    @Singleton
    fun provideJettyServer(
        servletContextHandler: ServletContextHandler,
        threadPool: QueuedThreadPool,
        httpConfig: HttpConfiguration
    ): Server {
        // Create server
        val server = Server(threadPool)

        // setup connector
        val http = ServerConnector(server, HttpConnectionFactory(httpConfig))
        http.port = 8000
        server.addConnector(http)

        // setup handler
        server.handler = servletContextHandler

        return server
    }

    override fun configure() {}
}