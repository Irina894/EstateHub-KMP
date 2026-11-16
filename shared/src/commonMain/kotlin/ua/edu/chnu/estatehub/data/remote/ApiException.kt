package ua.edu.chnu.estatehub.data.remote

/** Сервер відповів, але зі статусом не 2xx. */
class ApiException(val code: Int) : Exception("HTTP $code")
