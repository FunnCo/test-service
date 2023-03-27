package com.funnco.testservice.controller.test

import com.funnco.testservice.dto.DtoMapper
import com.funnco.testservice.dto.response.ResponseTestDto
import com.funnco.testservice.entity.TestingGroupEntity
import com.funnco.testservice.entity.composite_key.TestingGroupKey
import com.funnco.testservice.repository.TestRepository
import com.funnco.testservice.repository.TestingGroupRepository
import com.funnco.testservice.utils.DateUtil
import com.funnco.testservice.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import java.util.stream.Collectors


/**
 * Контроллер для обработки запросов студентов, связанных с тестами
 */
@RestController
@RequestMapping("/api/v2/test/student")
class StudentTestController {

    /**
     * Экземпляр TestRepository, используемый для доступа к данным тестов
     */
    @Autowired
    private lateinit var testRepository: TestRepository

    /**
     * Экземпляр DtoMapper, используемый для перевода сущностей в DTO или в обратном порядке
     */
    @Autowired
    private lateinit var dtoMapper: DtoMapper

    /**
     * Экземпляр TestRepository, используемый для доступа к данным о том, какие группы могут решать какие тесты
     */
    @Autowired
    private lateinit var testingGroupRepository: TestingGroupRepository

    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    /**
     * Получение списка доступных тестов для группы, к которой относится авторизованный пользователь.
     * @param httpServletRequest HTTP-запрос, содержащий токен авторизации пользователя.
     * @return ответ с http-статусом и списком DTO тестов, доступных для группы пользователя.
     */
    @GetMapping("/available")
    fun getAvailableTests(httpServletRequest: HttpServletRequest): ResponseEntity<List<ResponseTestDto>> {

        // Получение токена авторизации из заголовков запроса, и извлечение из него идентификатора группы
        val token = httpServletRequest.getHeader("Authorization").substring(7)
        val groupId = JwtUtil.getGroupId(token)

        val availableGroupTestKeys = testingGroupRepository.findById_GroupId(UUID.fromString(groupId))
            .stream()
            .map(TestingGroupEntity::id)
            .map(TestingGroupKey::testId)
            .collect(Collectors.toList())

        val availableTests = testRepository.findAllById(availableGroupTestKeys)
        val resultList = mutableListOf<ResponseTestDto>()
        for (test in availableTests) {
            // TODO: Вставить валидацию, что этот тест пользователь еще не прошел. GET запрос с сервиса статистики
            if (!DateUtil.isCurrentTimeInRange(test.startDate!!, test.deadlineDate!!, "Europe/Moscow")) {
                continue
            }
            resultList.add(dtoMapper.mapTestEntityToResponseDto(test))
        }

        return ResponseEntity.ok(resultList)
    }

}