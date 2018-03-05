package se.activout.kronslott.auth.session

import javax.annotation.Priority
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

@Provider
@Priority(Priorities.AUTHENTICATION - 1)
class HttpServletRequestPropertyFilter : ContainerRequestFilter {
    @Context
    var request: HttpServletRequest? = null

    override fun filter(requestContext: ContainerRequestContext) {
        requestContext.setProperty(HttpServletRequest::class.java.name, request)
    }
}