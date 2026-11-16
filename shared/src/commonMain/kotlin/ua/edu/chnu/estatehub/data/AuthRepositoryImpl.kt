package ua.edu.chnu.estatehub.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.SerializationException
import ua.edu.chnu.estatehub.data.local.TokenStorage
import ua.edu.chnu.estatehub.data.remote.ApiException
import ua.edu.chnu.estatehub.data.remote.AuthApi
import ua.edu.chnu.estatehub.data.remote.ProfileApi
import ua.edu.chnu.estatehub.domain.model.AuthFailure
import ua.edu.chnu.estatehub.domain.model.AuthResult
import ua.edu.chnu.estatehub.domain.model.AuthTokens
import ua.edu.chnu.estatehub.domain.model.UserProfile
import ua.edu.chnu.estatehub.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val storage: TokenStorage,
) : AuthRepository {
    private val log = Logger.withTag("AuthRepository")

    override val isAuthorized: StateFlow<Boolean> = storage.hasTokens

    override suspend fun signIn(username: String, password: String): AuthResult = try {
        val response = authApi.login(username, password)
        storage.saveUsername(username)
        storage.save(AuthTokens(response.accessToken, response.refreshToken))
        log.i { "signed in as $username" }
        AuthResult.Success(username)
    } catch (e: CancellationException) {
        throw e
    } catch (e: ApiException) {
        log.w { "sign in rejected: ${e.code}" }
        AuthResult.Failure(if (e.code == 400 || e.code == 401) AuthFailure.InvalidCredentials else AuthFailure.Server)
    } catch (e: SerializationException) {
        log.e(e) { "unexpected response" }
        AuthResult.Failure(AuthFailure.Server)
    } catch (e: Exception) {
        log.e(e) { "sign in failed" }
        AuthResult.Failure(AuthFailure.Network)
    }

    override suspend fun loadProfile(): UserProfile? = try {
        val dto = profileApi.me()
        UserProfile(dto.id, dto.username, "${dto.firstName} ${dto.lastName}".trim(), dto.email)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        log.w(e) { "profile request failed" }
        null
    }

    override fun currentUser(): String? = storage.username()

    override fun signOut() {
        storage.clear()
        log.i { "signed out" }
    }
}
