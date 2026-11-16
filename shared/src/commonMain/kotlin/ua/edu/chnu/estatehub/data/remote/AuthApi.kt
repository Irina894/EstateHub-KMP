package ua.edu.chnu.estatehub.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import ua.edu.chnu.estatehub.data.remote.dto.AuthResponseDto
import ua.edu.chnu.estatehub.data.remote.dto.LoginRequestDto
import ua.edu.chnu.estatehub.data.remote.dto.RefreshRequestDto
import ua.edu.chnu.estatehub.data.remote.dto.UserDto

/** Відкриті запити авторизації (без Bearer). */
class AuthApi(private val client: HttpClient) {
    suspend fun login(username: String, password: String): AuthResponseDto =
        client.post("auth/login") {
            setBody(LoginRequestDto(username, password, ApiConfig.ACCESS_TOKEN_TTL_MINUTES))
        }.bodyOrThrow()

    suspend fun refresh(refreshToken: String): AuthResponseDto =
        client.post("auth/refresh") {
            setBody(RefreshRequestDto(refreshToken, ApiConfig.ACCESS_TOKEN_TTL_MINUTES))
        }.bodyOrThrow()
}

/** Захищені запити: клієнт сам додає токен і оновлює його при 401. */
class ProfileApi(private val client: HttpClient) {
    suspend fun me(): UserDto = client.get("auth/me").bodyOrThrow()
}

private suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (!status.isSuccess()) throw ApiException(status.value)
    return body()
}
