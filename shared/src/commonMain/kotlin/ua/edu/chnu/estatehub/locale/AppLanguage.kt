package ua.edu.chnu.estatehub.locale

/** Мови застосунку. code — тег мови для ресурсів (values / values-uk). */
enum class AppLanguage(val code: String, val shortName: String, val nativeName: String) {
    UKRAINIAN("uk", "UA", "Українська"),
    ENGLISH("en", "EN", "English");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}
