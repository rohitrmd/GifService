package com.signals.guice

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.signals.server.SignalsServlet
import org.eclipse.jetty.proxy.ConnectHandler
import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.SecureRequestCustomizer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.SslConnectionFactory
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool

class ServerModule : AbstractModule() {

    @Singleton
    @Provides
    fun provideHttpConfiguration(): HttpConfiguration {
        // these values are hard coded but should be injected as guice props
        val httpsConfig = HttpConfiguration()
        httpsConfig.secureScheme = "https"
        httpsConfig.securePort = 8001
        httpsConfig.outputBufferSize = 32768
        httpsConfig.requestHeaderSize = 8192
        httpsConfig.responseHeaderSize = 8192
        httpsConfig.sendServerVersion = true
        httpsConfig.sendDateHeader = false

        httpsConfig.addCustomizer(SecureRequestCustomizer())
        return httpsConfig
    }

    @Provides
    fun provideSslContextFactory(): SslContextFactory {
        val sslContextFactory = SslContextFactory()
        sslContextFactory.keyStorePath = System.getProperty("user.dir") + "/keystore.jks"
        sslContextFactory.setKeyStorePassword("123456")
        sslContextFactory.setKeyManagerPassword("123456")
        return sslContextFactory
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
    fun provideServletContextHandler(signalsServlet: SignalsServlet): ServletContextHandler {
        val context = ServletContextHandler()
        context.addServlet(ServletHolder(signalsServlet), "/signals")
        return context
    }

    @Provides
    @Singleton
    fun createJettyServer(
        signalsServlet: SignalsServlet,
        threadPool: QueuedThreadPool,
        httpConfig: HttpConfiguration,
        sslContextFactory: SslContextFactory
    ): Server {
        // Create server
        val server = Server(threadPool)

        // setup connector
        val https = ServerConnector(
            server,
            SslConnectionFactory(sslContextFactory, "http/1.1"),
            HttpConnectionFactory(httpConfig)
        )
        https.port = 8001
        server.addConnector(https)

        // setup proxy handler
        val proxy = ConnectHandler()
        server.handler = proxy

        // setup proxy servlet
        val context = ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS)
        context.addServlet(ServletHolder(signalsServlet), "/signals")
        return server
    }

    override fun configure() {}
}