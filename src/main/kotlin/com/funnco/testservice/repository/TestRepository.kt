package com.funnco.testservice.repository

import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Интерфейс TestRepository расширяет базовый интерфейс CrudRepository,
 * который предоставляет набор CRUD-методов для работы с сущностями в базе данных.
 */
interface TestRepository : CrudRepository<TestEntity, UUID> {

    /**
     * Ищет список тестов по идентификатору автора.
     * @param id идентификатор автора, для которого необходимо найти список тестов.
     * @return список тестов, которые были созданы автором с указанным идентификатором.
     */
    fun findTestEntitiesByAuthorId(id: UUID): List<TestEntity>
}