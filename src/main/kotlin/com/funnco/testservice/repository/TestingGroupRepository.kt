package com.funnco.testservice.repository

import com.funnco.testservice.entity.TestingGroupEntity
import com.funnco.testservice.entity.composite_key.TestingGroupKey
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TestingGroupRepository: CrudRepository<TestingGroupEntity, TestingGroupKey> {
    fun findById_GroupId(groupId: UUID):List<TestingGroupEntity>
}