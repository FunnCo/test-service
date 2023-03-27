package com.funnco.testservice.repository

import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TestRepository : CrudRepository<TestEntity, UUID>{
    fun findTestEntitiesByAuthorId(id: UUID): List<TestEntity>
}