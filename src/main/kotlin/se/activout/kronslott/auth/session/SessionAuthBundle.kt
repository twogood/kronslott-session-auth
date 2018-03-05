package se.activout.kronslott.auth.session

import io.dropwizard.ConfiguredBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.server.session.SessionHandler

data class SessionSettings(
        val maxInactiveIntervalSeconds: Int = -1,
        val httpOnly: Boolean = true,
        val secure: Boolean = true
)

interface SessionAuthConfig {
    val sessionSettings: SessionSettings
}

open class SessionAuthBundle<C : SessionAuthConfig> : ConfiguredBundle<C> {
    override fun initialize(bootstrap: Bootstrap<*>) {
        // deliberately empty
    }

    override fun run(configuration: C, environment: Environment) {
        val settings = configuration.sessionSettings

        val sessionHandler = createSessionHandler()
        sessionHandler.maxInactiveInterval = settings.maxInactiveIntervalSeconds
        sessionHandler.sessionCookieConfig.isHttpOnly = settings.httpOnly
        sessionHandler.sessionCookieConfig.isSecure = settings.secure
        environment.servlets().setSessionHandler(sessionHandler)

        environment.jersey().register(HttpServletRequestPropertyFilter::class.java)
    }

    open fun createSessionHandler() = SessionHandler()
}