package com.signals.guice

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

class HttpClientModule : AbstractModule() {
    override fun configure() {}

    @Provides
    @Singleton
    fun provideConnectionPool(): PoolingHttpClientConnectionManager {
        return PoolingHttpClientConnectionManager().apply {
            defaultMaxPerRoute = 100
            maxTotal = Runtime.getRuntime().availableProcessors()
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(connectionManager: PoolingHttpClientConnectionManager): HttpClient {
        return HttpClients.custom().setConnectionManager(connectionManager).build()
    }

}