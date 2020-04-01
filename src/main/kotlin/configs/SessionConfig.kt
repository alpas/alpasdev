package dev.alpas.alpasdev.configs

import dev.alpas.Environment
import dev.alpas.session.SessionConfig
import java.time.Duration

class SessionConfig(env: Environment) : SessionConfig(env) {
    override val lifetime: Duration = Duration.ofDays(1095)
}
