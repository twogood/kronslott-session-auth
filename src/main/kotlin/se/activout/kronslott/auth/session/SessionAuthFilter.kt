package se.activout.kronslott.auth.session

import io.dropwizard.auth.AuthFilter
import java.security.Principal
import javax.annotation.Priority
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.Priorities
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext


abstract class AbstractSessionAuthFilter<P : Principal> : AuthFilter<HttpSession, P>() {
    override fun filter(requestContext: ContainerRequestContext) {
        val httpServletRequest: HttpServletRequest = checkNotNull(
                requestContext.getProperty(HttpServletRequest::class.java.name) as HttpServletRequest,
                { "Need HttpServletRequest as a property in ContainerRequestContext - please use HttpServletRequestPropertyFilter" })

        val session = httpServletRequest.getSession(false)
        if (!authenticate(requestContext, session, "SESSION")) {
            throw WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm))
        }
    }
}

@Priority(Priorities.AUTHENTICATION)
class SessionAuthFilter<P : Principal> : AbstractSessionAuthFilter<P>() {

    class Builder<P : Principal> : AuthFilter.AuthFilterBuilder<HttpSession, P, SessionAuthFilter<P>>() {
        override fun newInstance(): SessionAuthFilter<P> {
            return SessionAuthFilter()
        }
    }

}