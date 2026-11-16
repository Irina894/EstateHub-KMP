package ua.edu.chnu.estatehub.data.remote

/**
 * Налаштування мережі. Поки власний backend EstateHub не має авторизації,
 * використовується відкритий тестовий API https://dummyjson.com (POST /auth/login, /auth/refresh, GET /auth/me).
 * Для переходу на свій сервер достатньо змінити BASE_URL та DTO.
 */
object ApiConfig {
    const val BASE_URL = "https://dummyjson.com/"

    /** Короткий термін життя access-токена, щоб автооновлення було видно під час захисту. */
    const val ACCESS_TOKEN_TTL_MINUTES = 2
}
