package com.funnco.testservice.filter

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.Key
import java.util.*

@Component
class AuthFilter : OncePerRequestFilter() {

    val SECRET_KEY = "38792F423F4528482B4D6251655468566D597133743677397A24432646294A404E635266556A586E5A7234753778214125442A472D4B6150645367566B597033"

    private val logger = LoggerFactory.getLogger(this.javaClass.simpleName)

    private fun isTokenValid(token: String): Boolean{
        logger.info("trying to validate token $token")
        return try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).body.expiration.after(Date(System.currentTimeMillis()))
        } catch (e: Exception){
            logger.error("Error while parsing jwt key", e)
            false
        }
    }

    private fun isRequestAllowed(request: HttpServletRequest, token: String): Boolean{
        try {
            val role = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body["userRole"] as String
            if(request.requestURL.contains("student") && role == "STUDENT"){
                return true
            }
            if(request.requestURL.contains("teacher") && role == "TEACHER"){
                return true
            }
            logger.info("Request is not allowed")
            return false
        } catch (e: Exception){
            logger.error("Error while parsing jwt key", e)
            return false
        }
    }

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
            if(isTokenValid(authDetails) && isRequestAllowed(request, authDetails)){
                filterChain.doFilter(request, response)
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Bad auth details")
            }
        } catch (e: Exception) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: ${e.localizedMessage}")
        }
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}