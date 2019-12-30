Alpas doesn't bundle Redis or any Redis client, but it is really easy to integrate and use a Redis client
library, like [Jedis](https://github.com/xetorthio/jedis), in your app.

Let's see how we can easily integrate Jedis client in an app using a service provider.

<div class="ordered-list">

1. Crate a new service provider using `make:provider` Alpas command:

```bash

$ alpas make:provider JedisServiceProvider

```

2. Register an instance of `JedisPool` in the new created `JedisServiceProvider` class making sure that the pool 
gets closed when the app is stopped:

<span class="line-numbers" data-start="8">

```kotlin

// providers/JedisServiceProvider.kt
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

```

</span>

3. Ask the pool to create a Jedis client and use it to get or set any values in and out of Redis:

<span class="line-numbers" data-start="15">

```kotlin

fun get(page: String, markdown: Markdown): String {
    return jedisPool.resource.use { jedis ->
       return jedis.hget("docs", page)?: markdown.convert(page).also { 
           jedis.hset("docs", page, it) 
       }
    }
}

```

</span>

</div>

**That's all there is!**
