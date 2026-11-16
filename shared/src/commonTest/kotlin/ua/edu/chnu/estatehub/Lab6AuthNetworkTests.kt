package ua.edu.chnu.estatehub

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import ua.edu.chnu.estatehub.data.local.TokenStorage
import ua.edu.chnu.estatehub.data.remote.AuthApi
import ua.edu.chnu.estatehub.data.remote.TokenRefresher
import ua.edu.chnu.estatehub.data.remote.createAuthorizedClient
import ua.edu.chnu.estatehub.data.remote.createPublicClient
import ua.edu.chnu.estatehub.domain.model.AuthTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

private class FakeTokenStorage(initial: AuthTokens?) : TokenStorage {
    private var tokens = initial
    private val flag = MutableStateFlow(initial != null)
    override val hasTokens: StateFlow<Boolean> = flag
    override suspend fun load() = tokens
    override suspend fun save(tokens: AuthTokens) { this.tokens = tokens; flag.value = true }
    override suspend fun saveUsername(username: String) {}
    override fun username(): String? = null
    override fun clear() { tokens = null; flag.value = false }
}

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

class TokenRefreshTests {

    private fun apiEngine(refreshStatus: HttpStatusCode, calls: MutableList<String>) = MockEngine { request ->
        val path = request.url.encodedPath
        calls += path
        when {
            path.endsWith("auth/refresh") ->
                if (refreshStatus == HttpStatusCode.OK)
                    respond("""{"accessToken":"new","refreshToken":"newRefresh"}""", HttpStatusCode.OK, jsonHeaders)
                else respond("""{"message":"bad"}""", refreshStatus, jsonHeaders)
            path.endsWith("auth/me") ->
                if (request.headers[HttpHeaders.Authorization] == "Bearer new")
                    respond("""{"id":1,"username":"emilys"}""", HttpStatusCode.OK, jsonHeaders)
                else respond("""{"message":"Token Expired!"}""", HttpStatusCode.Unauthorized, jsonHeaders)
            else -> respond("", HttpStatusCode.NotFound)
        }
    }

    @Test
    fun expiredTokenIsRefreshedAndRequestRepeated() = runTest {
        val calls = mutableListOf<String>()
        val storage = FakeTokenStorage(AuthTokens("old", "oldRefresh"))
        val engine = apiEngine(HttpStatusCode.OK, calls)
        val refresher = TokenRefresher(AuthApi(createPublicClient(engine = engine)), storage)
        val client = createAuthorizedClient(storage, refresher, engine = engine)

        val response = client.get("auth/me")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"id":1,"username":"emilys"}""", response.bodyAsText())
        assertEquals(AuthTokens("new", "newRefresh"), storage.load())
        assertEquals(1, calls.count { it.endsWith("auth/refresh") })
    }

    @Test
    fun rejectedRefreshClearsSession() = runTest {
        val calls = mutableListOf<String>()
        val storage = FakeTokenStorage(AuthTokens("old", "oldRefresh"))
        val engine = apiEngine(HttpStatusCode.Unauthorized, calls)
        val refresher = TokenRefresher(AuthApi(createPublicClient(engine = engine)), storage)
        val client = createAuthorizedClient(storage, refresher, engine = engine)

        val response = client.get("auth/me")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertNull(storage.load())
        assertFalse(storage.hasTokens.value)
    }
}
