package ua.edu.chnu.estatehub.data.remote

import co.touchlab.kermit.Logger as Kermit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.takeFrom
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ua.edu.chnu.estatehub.data.local.TokenStorage

val ApiJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private const val TIMEOUT_MS = 15_000L

private fun HttpClientConfig<*>.configure(json: Json) {
    install(ContentNegotiation) { json(json) }
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MS
        connectTimeoutMillis = TIMEOUT_MS
    }
    install(Logging) {
        level = LogLevel.INFO
        sanitizeHeader { it == HttpHeaders.Authorization }
        logger = object : Logger {
            override fun log(message: String) {
                Kermit.withTag("Http").d { message }
            }
        }
    }
    defaultRequest {
        url(ApiConfig.BASE_URL)
        contentType(ContentType.Application.Json)
    }
}

/** Клієнт без токенів: логін і оновлення токена (інакше було б зациклення 401 -> refresh -> 401). */
fun createPublicClient(json: Json = ApiJson, engine: HttpClientEngine? = null): HttpClient =
    if (engine == null) HttpClient { configure(json) } else HttpClient(engine) { configure(json) }

/**
 * Клієнт для захищених запитів. Перехоплювач HttpSend:
 *  1) додає заголовок Authorization: Bearer <access>;
 *  2) якщо сервер відповів 401 - один раз оновлює токени (під Mutex) і повторює запит;
 *  3) якщо оновити не вдалося - повертає початкову 401, а сесія очищується в TokenRefresher.
 */
fun createAuthorizedClient(
    storage: TokenStorage,
    refresher: TokenRefresher,
    json: Json = ApiJson,
    engine: HttpClientEngine? = null,
): HttpClient {
    val client = if (engine == null) HttpClient { configure(json) } else HttpClient(engine) { configure(json) }
    client.plugin(HttpSend).intercept { request ->
        val tokens = storage.load()
        if (tokens != null) request.bearer(tokens.accessToken)

        val call = execute(request)
        if (call.response.status != HttpStatusCode.Unauthorized || tokens == null) return@intercept call

        val fresh = refresher.refresh(failedAccessToken = tokens.accessToken) ?: return@intercept call
        val retry = HttpRequestBuilder().takeFrom(request)
        retry.bearer(fresh)
        execute(retry)
    }
    return client
}

private fun HttpRequestBuilder.bearer(token: String) {
    headers.remove(HttpHeaders.Authorization)
    header(HttpHeaders.Authorization, "Bearer $token")
}
