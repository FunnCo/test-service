package com.funnco.testservice.repository

import com.funnco.testservice.entity.QuestionEntity
import com.funnco.testservice.entity.TestEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QuestionRepository : CrudRepository<QuestionEntity, UUID> {

}