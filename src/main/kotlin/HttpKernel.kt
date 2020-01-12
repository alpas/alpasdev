package dev.alpas.alpasdev

import dev.alpas.Application
import dev.alpas.ServiceProvider
import dev.alpas.alpasdev.providers.DocumentationServiceProvider
import dev.alpas.alpasdev.providers.JedisServiceProvider
import dev.alpas.alpasdev.providers.ViewServiceProvider
import dev.alpas.encryption.EncryptionServiceProvider
import dev.alpas.hashing.HashServiceProvider
import dev.alpas.http.HttpKernel
import dev.alpas.logging.LoggerServiceProvider
import dev.alpas.ozone.OzoneServiceProvider
import dev.alpas.queue.QueueServiceProvider
import dev.alpas.routing.RouteServiceProvider
import dev.alpas.session.SessionServiceProvider
import kotlin.reflect.KClass

@Suppress("unused")
class HttpKernel : HttpKernel() {
    override fun serviceProviders(app: Application): Iterable<KClass<out ServiceProvider>> {
        return listOf(
            LoggerServiceProvider::class,
            EncryptionServiceProvider::class,
            HashServiceProvider::class,
            SessionServiceProvider::class,
            RouteServiceProvider::class,
            ViewServiceProvider::class,
            OzoneServiceProvider::class,
            QueueServiceProvider::class,
            JedisServiceProvider::class,
            DocumentationServiceProvider::class
        )
    }
}
