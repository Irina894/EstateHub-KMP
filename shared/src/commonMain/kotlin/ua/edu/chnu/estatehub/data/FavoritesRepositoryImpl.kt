package ua.edu.chnu.estatehub.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ua.edu.chnu.estatehub.domain.repository.FavoritesRepository

/** Обрані зберігаються рядком "1,3,5" у multiplatform-settings і переживають перезапуск. */
class FavoritesRepositoryImpl(private val settings: Settings) : FavoritesRepository {

    private val _ids = MutableStateFlow(load())
    override val favoriteIds: StateFlow<Set<Long>> = _ids.asStateFlow()

    override fun toggle(id: Long) {
        _ids.update { if (id in it) it - id else it + id }
        settings.putString(KEY, _ids.value.joinToString(","))
    }

    private fun load(): Set<Long> =
        settings.getStringOrNull(KEY)
            ?.split(',')
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?.toSet()
            ?: emptySet()

    private companion object {
        const val KEY = "favorite_ids"
    }
}
