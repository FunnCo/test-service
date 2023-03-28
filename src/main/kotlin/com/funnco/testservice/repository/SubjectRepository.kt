package com.funnco.testservice.repository

import com.funnco.testservice.entity.SubjectEntity
import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Интерфейс SubjectRepository расширяет базовый интерфейс CrudRepository,
 * который предоставляет набор CRUD-методов для работы с сущностями в базе данных.
 */
interface SubjectRepository : CrudRepository<SubjectEntity, UUID> {

    /**
     * Проверяет существование сущности по имени.
     * @param name имя сущности, которую необходимо проверить на существование.
     * @return true, если сущность существует, иначе false.
     */
    fun existsByName(name: String): Boolean

}