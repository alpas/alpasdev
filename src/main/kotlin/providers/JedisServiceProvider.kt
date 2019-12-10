package dev.alpas.alpasdev.providers

import dev.alpas.Application
import dev.alpas.ServiceProvider
import dev.alpas.make
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class JedisServiceProvider : ServiceProvider {
    override fun register(app: Application) {
        app.bind(JedisPool(JedisPoolConfig(), "localhost"))
    }

    override fun stop(app: Application) {
        app.logger.debug { "Closing Jedis Pool" }
        app.make<JedisPool>().close()
    }
}
