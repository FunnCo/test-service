package com.funnco.testservice.repository

import com.funnco.testservice.entity.QuestionTypeEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QuestionTypeRepository : CrudRepository<QuestionTypeEntity, UUID> {

}