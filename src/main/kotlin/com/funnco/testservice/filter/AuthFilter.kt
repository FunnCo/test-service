package com.funnco.testservice.filter

import com.funnco.testservice.utils.JwtUtil

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.Key
import java.util.*

/**
 * Фильтр авторизации для проверки токена доступа к защищенным ресурсам.
 */
@Component
class AuthFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)

    /**
     * Проверка валидности токена.
     * @param token токен доступа
     * @return true, если токен валиден, иначе false
     */
    private fun isTokenValid(token: String): Boolean {
        return try {
            JwtUtil.getExpirationDate(token).after(Date(System.currentTimeMillis()))
        } catch (e: Exception) {
            logger.error("Error while parsing jwt key", e)
            false
        }
    }

    /**
     * Проверка прав доступа на основании токена и URL запроса.
     * @param request объект запроса
     * @param token токен доступа
     * @return true, если доступ разрешен, иначе false
     */
    private fun isRequestAllowed(request: HttpServletRequest, token: String): Boolean {
        try {
            val role = JwtUtil.getUserRole(token)
            if (request.requestURL.contains("student")) {
                return true
            }
            if (request.requestURL.contains("teacher") && role == "TEACHER") {
                return true
            }
            if (request.requestURL.contains("subject")) {
                return true
            }
            if (request.requestURL.contains("question")) {
                return true
            }
            logger.info("Request is not allowed")
            return false
        } catch (e: Exception) {
            logger.error("Error while parsing jwt key", e)
            return false
        }
    }

    /**
     * Фильтрация запроса для проверки авторизации.
     * @param request объект запроса
     * @param response объект ответа
     * @param filterChain цепочка фильтров
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var authDetails = request.getHeader("Authorization")
        if (authDetails == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad auth details")
            return
        }

        try {
            authDetails = authDetails.substring(7);
            if (isTokenValid(authDetails) && isRequestAllowed(request, authDetails)) {
                filterChain.doFilter(request, response)
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad auth details")
            }
        } catch (e: Exception) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: ${e.localizedMessage}")
        }
    }
}