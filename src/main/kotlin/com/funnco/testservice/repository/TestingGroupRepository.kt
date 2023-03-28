package com.funnco.testservice.repository

import com.funnco.testservice.entity.TestingGroupEntity
import com.funnco.testservice.entity.composite_key.TestingGroupKey
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Интерфейс TestingGroupRepository расширяет базовый интерфейс CrudRepository,
 * который предоставляет набор CRUD-методов для работы с сущностями в базе данных.
 */
interface TestingGroupRepository : CrudRepository<TestingGroupEntity, TestingGroupKey> {

    /**

     * Ищет список групп тестирования по идентификатору группы.
     * @param groupId идентификатор группы, для которой необходимо найти список групп тестирования.
     * @return список групп тестирования, которые принадлежат группе с указанным идентификатором.
     */
    fun findById_GroupId(groupId: UUID): List<TestingGroupEntity>
}