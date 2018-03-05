package se.activout.kronslott.auth.session

import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

object SessionUserHelper {

    private const val USER_IN_SESSION = "USER"

    fun createSession(httpServletRequest: HttpServletRequest, user: Principal) {
        httpServletRequest.getSession(true).setAttribute(USER_IN_SESSION, user)
    }

    fun destroySession(httpServletRequest: HttpServletRequest) {
        val session = httpServletRequest.getSession(false)
        session?.apply {
            removeAttribute(USER_IN_SESSION)
            invalidate()
        }
    }

    fun <P : Principal> getUser(session: HttpSession): P? {
        @Suppress("UNCHECKED_CAST")
        return session.getAttribute(SessionUserHelper.USER_IN_SESSION) as P?
    }
}