package com.funnco.testservice.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

/**

Класс для работы с JWT токенами.
 */
object JwtUtil {

    /**
     * Секретный ключ для создания и валидации JWT токенов.
     */
    val SECRET_KEY =
        "38792F423F4528482B4D6251655468566D597133743677397A24432646294A404E635266556A586E5A7234753778214125442A472D4B6150645367566B597033"

    /**
     * Получает роль пользователя из JWT токена.
     * @param token JWT токен.
     * @return Роль пользователя.
     */
    fun getUserRole(token: String): String {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
            .parseClaimsJws(token).body["userRole"] as String
    }

    /**
     * Получает ID пользователя из JWT токена.
     * @param token JWT токен.
     * @return ID пользователя.
     */
    fun getUserId(token: String): String {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body.subject
    }

    /**
     * Получает ID группы пользователя из JWT токена.
     * @param token JWT токен.
     * @return ID группы пользователя.
     */
    fun getGroupId(token: String): String {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
            .parseClaimsJws(token).body["userGroupId"] as String
    }

    /**
     * Получает дату и время истечения срока действия JWT токена.
     * @param token JWT токен.
     * @return Дата и время истечения срока действия JWT токена.
     */
    fun getExpirationDate(token: String): Date {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body.expiration
    }

    /**
     * Возвращает ключ для подписи JWT токена на основе SECRET_KEY.
     * @return Ключ для подписи JWT токена.
     */
    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}