package com.funnco.testservice.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

object JwtUtil {

    val SECRET_KEY = "38792F423F4528482B4D6251655468566D597133743677397A24432646294A404E635266556A586E5A7234753778214125442A472D4B6150645367566B597033"

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getUserRole(token: String): String{
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body["userRole"] as String
    }

    fun getUserId(token: String): String{
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body.subject
    }

    fun getGroupId(token: String): String{
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body["userGroupId"] as String
    }

    fun getExpirationDate(token: String): Date {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body.expiration
    }

}