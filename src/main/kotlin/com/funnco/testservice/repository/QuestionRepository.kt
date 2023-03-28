package com.funnco.testservice.repository

import com.funnco.testservice.entity.QuestionEntity
import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Интерфейс QuestionRepository расширяет базовый интерфейс CrudRepository,
 * который предоставляет набор CRUD-методов для работы с сущностями в базе данных.
 */
interface QuestionRepository : CrudRepository<QuestionEntity, UUID> {

    /**
     * Метод findAllByTest_TestId осуществляет поиск и возвращает список всех вопросов
     * для теста с указанным идентификатором.
     * @param id идентификатор теста
     * @return список сущностей QuestionEntity для теста с заданным идентификатором
     */
    fun findAllByTest_TestId(id: UUID): List<QuestionEntity>
}