package se.activout.kronslott.auth.session

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.auth.Auth
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import io.dropwizard.util.Duration
import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.Test
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


class TestUser : Principal {
    private val name: String = "*USERNAME*"

    override fun getName(): String {
        return name
    }

}

class TestConfiguration : Configuration(), SessionAuthConfig {
    @Valid
    @JsonProperty("session")
    override val sessionSettings: SessionSettings = SessionSettings()
}

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
class TestResource {
    @POST
    @Path("login")
    fun login(@Context httpServletRequest: HttpServletRequest, user: TestUser): TestUser {
        SessionUserHelper.createSession(httpServletRequest, user)
        return user;
    }

    @DELETE
    @Path("session")
    fun logout(@Context httpServletRequest: HttpServletRequest) {
        SessionUserHelper.destroySession(httpServletRequest)
    }

    @GET
    @Path("me")
    fun getMe(@Auth user: TestUser): TestUser {
        return user;
    }
}

class TestApp : Application<TestConfiguration>() {
    override fun initialize(bootstrap: Bootstrap<TestConfiguration>) {
        bootstrap.addBundle(SessionAuthBundle<TestConfiguration>())
    }

    override fun run(configuration: TestConfiguration, environment: Environment) {
        environment.jersey().apply {

            register(TestResource::class.java)

            register(AuthDynamicFeature(SessionAuthFilter.Builder<TestUser>()
                    .setAuthenticator(SessionAuthenticator())
                    .buildAuthFilter()))

            register(AuthValueFactoryProvider.Binder(TestUser::class.java))
        }
    }
}

class IntegrationTest {

    companion object {
        @JvmField
        @ClassRule
        val rule = DropwizardAppRule<TestConfiguration>(TestApp::class.java, ResourceHelpers.resourceFilePath("test-config.yml"))
    }

    @Test
    fun testLoginLogout() {
        val jerseyClientConfiguration = JerseyClientConfiguration()
        jerseyClientConfiguration.isCookiesEnabled = true
        jerseyClientConfiguration.connectionTimeout = Duration.seconds(60)
        jerseyClientConfiguration.timeout = Duration.seconds(60)
        val client = JerseyClientBuilder(rule.environment)
                .using(jerseyClientConfiguration)
                .build("testLoginLogout")

        assertThat(getMe(client).status).isEqualTo(401)
        login(client)
        assertThat(getMe(client).status).isEqualTo(200)
        logout(client)
        assertThat(getMe(client).status).isEqualTo(401)
    }

    private fun getMe(client: Client): Response {
        return client.target(
                url("me"))
                .request()
                .get()
    }

    private fun login(client: Client) {
        val response = client.target(
                url("login"))
                .request()
                .post(Entity.json(TestUser()))
        assertThat(response.status).isEqualTo(200)
    }

    private fun logout(client: Client) {
        val response = client.target(
                url("session"))
                .request()
                .delete()
        assertThat(response.status).isEqualTo(204)
    }

    private fun url(path: String) = String.format("http://localhost:%d/$path", rule.localPort)
}