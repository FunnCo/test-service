package com.funnco.testservice.controller.question

import com.funnco.testservice.dto.DtoMapper
import com.funnco.testservice.dto.request.NewQuestionTypeDto
import com.funnco.testservice.dto.response.ResponseTypeDto
import com.funnco.testservice.repository.QuestionTypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Контроллер для работы с типами вопросов
 */
@RestController
@RequestMapping(("/api/v2/test/question/type"))
class QuestionTypeController {

    /**
     * Экземпляр DtoMapper, используемый для перевода сущностей в DTO или в обратном порядке.
     */
    @Autowired
    private lateinit var dtoMapper: DtoMapper

    /**
     * Экземпляр QuestionTypeRepository, используемый для доступа к данным о типах вопросов.
     */
    @Autowired
    private lateinit var questionTypeRepository: QuestionTypeRepository

    /**
     * Добавление нового типа вопроса
     * @param newTypeDto данные нового типа вопроса
     * @return ResponseEntity<ResponseTypeDto> созданный тип вопроса
     * @throws ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad name") в случае если передано некорректное имя типа вопроса
     * @throws ResponseStatusException(HttpStatus.CONFLICT, "type with such type already exists") в случае если тип вопроса с таким именем уже существует
     */
    @PostMapping
    fun addNewQuestionType(
        @RequestBody newTypeDto: NewQuestionTypeDto
    ): ResponseEntity<ResponseTypeDto> {
        if (!newTypeDto.isObjectValid()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad name")
        }

        if (questionTypeRepository.existsByName(newTypeDto.name!!)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "type with such type already exists")
        }

        val newEntity = questionTypeRepository.save(dtoMapper.mapQuestionTypeDtoToEntity(newTypeDto))
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.mapQuestionTypeEntityToDto(newEntity))
    }

    /**
     * Получение всех типов вопросов
     * @return ResponseEntity<List<ResponseTypeDto>> список типов вопросов
     */
    @GetMapping
    fun getAllTypes(): ResponseEntity<List<ResponseTypeDto>> {
        val result = mutableListOf<ResponseTypeDto>()
        for (type in questionTypeRepository.findAll()) {
            result.add(dtoMapper.mapQuestionTypeEntityToDto(type))
        }
        return ResponseEntity.ok(result)
    }

    /**
     * Получение типа вопроса по его UUID.
     * @param typeId Строковое представление UUID типа вопросса
     * @return ResponseEntity<ResponseTypeDto> тип вопроса
     * @throws ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad id") в случае если передан некорректный ID типа вопроса
     */
    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{typeId}")
    fun getType(
        @PathVariable typeId: String
    ): ResponseEntity<ResponseTypeDto> {

        val entity = questionTypeRepository.findById(UUID.fromString(typeId)).getOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad id")

        return ResponseEntity.ok(dtoMapper.mapQuestionTypeEntityToDto(entity))
    }
}