package com.funnco.testservice.repository

import com.funnco.testservice.entity.SubjectEntity
import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SubjectRepository : CrudRepository<SubjectEntity, UUID> {
    fun existsByName(name: String): Boolean

}