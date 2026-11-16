package ua.edu.chnu.estatehub.data.remote

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ua.edu.chnu.estatehub.data.local.TokenStorage
import ua.edu.chnu.estatehub.domain.model.AuthTokens

/**
 * Автоматичне оновлення токенів. Mutex гарантує один refresh, навіть якщо кілька запитів
 * одночасно отримали 401: решта побачить уже нові токени в сховищі й не викликатиме сервер удруге.
 */
class TokenRefresher(
    private val api: AuthApi,
    private val storage: TokenStorage,
) {
    private val log = Logger.withTag("TokenRefresher")
    private val mutex = Mutex()

    /** @return новий access-токен або null, якщо оновити не вдалося. */
    suspend fun refresh(failedAccessToken: String): String? = mutex.withLock {
        val current = storage.load() ?: return null
        if (current.accessToken != failedAccessToken) {
            return current.accessToken // паралельний запит уже оновив токени
        }
        try {
            val response = api.refresh(current.refreshToken)
            val fresh = AuthTokens(response.accessToken, response.refreshToken)
            storage.save(fresh)
            log.i { "tokens refreshed" }
            fresh.accessToken
        } catch (e: CancellationException) {
            throw e
        } catch (e: ApiException) {
            // refresh-токен відхилено: сесії кінець, користувача поверне на екран входу
            log.w { "refresh rejected (${e.code}), clearing session" }
            storage.clear()
            null
        } catch (e: Exception) {
            // немає мережі: сесію не чіпаємо, спробуємо наступного разу
            log.w(e) { "refresh failed" }
            null
        }
    }
}
