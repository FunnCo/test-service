package com.funnco.testservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Главный класс приложения Test Service, использующий Spring Boot.
 * Класс аннотирован как @SpringBootApplication, что указывает Spring Boot
 * на то, что этот класс является точкой входа в приложение и содержит все
 * необходимые настройки для работы Spring Boot.
 */
@SpringBootApplication
class TestServiceApplication

/**
 * Функция main(), которая запускает приложение Test Service.
 * @param args Массив строковых аргументов, переданных при запуске приложения.
 */
fun main(args: Array<String>) {
    runApplication<TestServiceApplication>(*args)
}
