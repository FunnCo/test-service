package com.funnco.testservice.config

import com.funnco.testservice.filter.AuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


/**
 * Класс, представляющий конфигурацию безопасности веб-приложения.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(val filter: AuthFilter) {

    /**
     * Метод, в котором происходит настройка безопасности приложения.
     * @return цепочку фильтрова предшествующих обработке запроса.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain{
        return http.csrf().disable().addFilterBefore(filter, BasicAuthenticationFilter::class.java).build()
    }
}