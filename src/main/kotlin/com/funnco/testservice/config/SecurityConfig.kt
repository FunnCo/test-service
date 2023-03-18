package com.funnco.testservice.config

import com.funnco.testservice.filter.AuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val filter: AuthFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain{
        return http.addFilterBefore(filter, BasicAuthenticationFilter::class.java).build()
    }
}