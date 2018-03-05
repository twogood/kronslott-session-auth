package se.activout.kronslott.auth.session

import io.dropwizard.auth.Authenticator
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpSession

class SessionAuthenticator<P : Principal> : Authenticator<HttpSession, P> {
    override fun authenticate(credentials: HttpSession?): Optional<P> {
        if (credentials == null) {
            return Optional.empty()
        }

        return Optional.ofNullable(SessionUserHelper.getUser(credentials))
    }
}