package com.funnco.testservice.dto

import com.funnco.testservice.dto.request.NewQuestionDto
import com.funnco.testservice.dto.request.NewQuestionTypeDto
import com.funnco.testservice.dto.request.NewSubjectDto
import com.funnco.testservice.dto.request.NewTestDto
import com.funnco.testservice.dto.universal.QuestionDto
import com.funnco.testservice.dto.response.ResponseTestDto
import com.funnco.testservice.dto.response.ResponseTypeDto
import com.funnco.testservice.dto.universal.SubjectDto
import com.funnco.testservice.entity.QuestionEntity
import com.funnco.testservice.entity.QuestionTypeEntity
import com.funnco.testservice.entity.SubjectEntity
import com.funnco.testservice.entity.TestEntity
import com.funnco.testservice.repository.QuestionTypeRepository
import com.funnco.testservice.repository.SubjectRepository
import com.funnco.testservice.repository.TestRepository
import com.funnco.testservice.utils.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.UUID


/**

Класс DtoMapper предназначен для преобразования объектов различных DTO и сущностей базы данных

в другие объекты DTO и сущности.
 */
@Component
class DtoMapper {

    @Autowired
    private lateinit var subjectRepository: SubjectRepository

    @Autowired
    private lateinit var questionTypeRepository: QuestionTypeRepository

    @Autowired
    private lateinit var testRepository: TestRepository

    /**
     * Метод mapNewTestDtoToEntity() принимает объект NewTestDto и UUID автора теста в строковом формате,
     * и возвращает сущность TestEntity на основе переданных данных.
     * @param dto объект NewTestDto, который содержит данные для создания нового теста.
     * @param authorUUID UUID автора теста в строковом формате.
     *  @return сущность TestEntity на основе переданных данных.
     */
    fun mapNewTestDtoToEntity(dto: NewTestDto, authorUUID: String): TestEntity {
        val result = TestEntity()
        result.criteriaExcellent = dto.criteriaExcellent
        result.criteriaGood = dto.criteriaGood
        result.criteriaPass = dto.criteriaPass
        result.description = dto.description
        result.title = dto.title
        result.subject = subjectRepository.findById(UUID.fromString(dto.subjectId)).orElseThrow()
        result.authorId = UUID.fromString(authorUUID)
        result.duration = dto.duration
        result.startDate = DateUtil.parseStringToDate(dto.startDate!!)
        result.deadlineDate = DateUtil.parseStringToDate(dto.deadlineDate!!)

        return result
    }

    /**
     * Метод mapTestEntityToResponseDto() принимает сущность TestEntity и возвращает объект ResponseTestDto,
     * содержащий данные теста.
     * @param entity сущность TestEntity, содержащая данные теста.
     * @return объект ResponseTestDto, содержащий данные теста.
     */
    fun mapTestEntityToResponseDto(entity: TestEntity): ResponseTestDto {
        return ResponseTestDto(
            entity.testId.toString(),
            entity.title,
            entity.description,
            entity.authorId.toString(),
            entity.subject!!.name,
            entity.startDate,
            entity.deadlineDate,
            entity.duration
        )
    }

    /**
     * Метод mapSubjectEntityToDto() принимает сущность SubjectEntity и возвращает объект SubjectDto,
     * содержащий данные о предмете.
     * @param entity сущность SubjectEntity, содержащая данные о предмете.
     * @return объект SubjectDto, содержащий данные о предмете.
     */
    fun mapSubjectEntityToDto(entity: SubjectEntity): SubjectDto {
        return SubjectDto(
            entity.subjectId.toString(),
            entity.name
        )
    }

    /**
     * Метод mapSubjectDtoToEntity() принимает объект NewSubjectDto и возвращает сущность SubjectEntity
     * на основе переданных данных.
     * @param dto объект NewSubjectDto, который содержит данные для создания нового предмета.
     * @return сущность SubjectEntity на основе переданных данных.
     */
    fun mapSubjectDtoToEntity(dto: NewSubjectDto): SubjectEntity {
        val entity = SubjectEntity()
        entity.name = dto.name
        return entity
    }

    /**
     * Преобразует объект типа NewQuestionTypeDto в соответствующую сущность QuestionTypeEntity.
     * @param dto объект типа NewQuestionTypeDto
     * @return сущность QuestionTypeEntity
     */
    fun mapQuestionTypeDtoToEntity(dto: NewQuestionTypeDto): QuestionTypeEntity {
        val entity = QuestionTypeEntity()
        entity.name = dto.name
        return entity
    }

    /**
     * Преобразует сущность типа QuestionTypeEntity в объект типа ResponseTypeDto.
     * @param entity сущность типа QuestionTypeEntity
     * @return объект типа ResponseTypeDto
     */
    fun mapQuestionTypeEntityToDto(entity: QuestionTypeEntity): ResponseTypeDto {
        return ResponseTypeDto(entity.typeId, entity.name)
    }

    /**
     * Преобразует объект типа NewQuestionDto в соответствующую сущность QuestionEntity.
     * @param dto объект типа NewQuestionDto
     * @param testId идентификатор теста
     * @return сущность QuestionEntity
     * @throws ResponseStatusException если идентификатор типа вопроса или теста неверен
     */
    fun mapNewQuestionDtoToEntity(dto: NewQuestionDto, testId: String): QuestionEntity {
        val entity = QuestionEntity()
        entity.description = dto.description
        entity.body = dto.body
        entity.type = questionTypeRepository.findById(UUID.fromString(dto.typeId)).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Question type not found")
        }
        entity.test = testRepository.findById(UUID.fromString(testId)).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found")
        }
        entity.weight = dto.weight
        entity.number = dto.number
        return entity
    }

    /**
     * Преобразует сущность типа QuestionEntity в объект типа QuestionDto.
     * @param entity сущность типа QuestionEntity
     * @return объект типа QuestionDto
     */
    fun mapQuestionEntityToDto(entity: QuestionEntity): QuestionDto {
        return QuestionDto(
            entity.questionId.toString(),
            entity.description,
            entity.type?.typeId.toString(),
            entity.body,
            entity.number
        )
    }

}
