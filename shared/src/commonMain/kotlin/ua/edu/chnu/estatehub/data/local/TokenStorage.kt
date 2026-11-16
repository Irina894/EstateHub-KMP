package ua.edu.chnu.estatehub.data.local

import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.edu.chnu.estatehub.domain.model.AuthTokens

/** Сховище сесії. hasTokens = ознака авторизованого користувача для навігації. */
interface TokenStorage {
    val hasTokens: StateFlow<Boolean>
    suspend fun load(): AuthTokens?
    suspend fun save(tokens: AuthTokens)
    suspend fun saveUsername(username: String)
    fun username(): String?
    /** Синхронно, бо викликається з SignOutUseCase. */
    fun clear()
}

/**
 * Токени зберігаються в KSafe: значення шифруються AES-GCM,
 * ключ лежить в Android Keystore (на desktop - у сховищі ОС).
 */
class KSafeTokenStorage(private val ksafe: KSafe) : TokenStorage {
    private val _hasTokens = MutableStateFlow(ksafe.getDirect(KEY_ACCESS, "").isNotEmpty())
    override val hasTokens: StateFlow<Boolean> = _hasTokens.asStateFlow()

    override suspend fun load(): AuthTokens? {
        val access = ksafe.get(KEY_ACCESS, "")
        val refresh = ksafe.get(KEY_REFRESH, "")
        return if (access.isEmpty() || refresh.isEmpty()) null else AuthTokens(access, refresh)
    }

    override suspend fun save(tokens: AuthTokens) {
        ksafe.put(KEY_ACCESS, tokens.accessToken)
        ksafe.put(KEY_REFRESH, tokens.refreshToken)
        _hasTokens.value = true
    }

    override suspend fun saveUsername(username: String) {
        ksafe.put(KEY_USER, username)
    }

    override fun username(): String? = ksafe.getDirect(KEY_USER, "").ifEmpty { null }

    override fun clear() {
        ksafe.deleteDirect(KEY_ACCESS)
        ksafe.deleteDirect(KEY_REFRESH)
        ksafe.deleteDirect(KEY_USER)
        _hasTokens.value = false
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_USER = "username"
    }
}
