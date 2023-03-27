package com.funnco.testservice.controller.question

import com.funnco.testservice.dto.DtoMapper
import com.funnco.testservice.dto.request.SolvedTestDto
import com.funnco.testservice.dto.universal.QuestionDto
import com.funnco.testservice.entity.composite_key.TestingGroupKey
import com.funnco.testservice.kafka.KafkaAnswerReportService
import com.funnco.testservice.model.AnswerReport
import com.funnco.testservice.repository.QuestionRepository
import com.funnco.testservice.repository.TestRepository
import com.funnco.testservice.repository.TestingGroupRepository
import com.funnco.testservice.service.QuestionService
import com.funnco.testservice.utils.DateUtil
import com.funnco.testservice.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
 * Контроллер для обработки запросов, связанных с вопросами в тесте
 */
@RestController
@RequestMapping("/api/v2/test/question")
class QuestionController {

    /**
     * Экземпляр TestingGroupRepository, используемый для доступа к данным группы тестирования
     */
    @Autowired
    private lateinit var testingGroupRepository: TestingGroupRepository

    /**
     *Экземпляр QuestionRepository, используемый для доступа к данным вопросоа
     */
    @Autowired
    private lateinit var questionRepository: QuestionRepository

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
     * Экземпляр QuestionService, используемый для обработки даанных о вопросах
     */
    @Autowired
    private lateinit var questionService: QuestionService

    /**
     * Экземпляр kafkaAnswerReportService, используемый для отправки отчетов о выполнных тестах в брокер
     */
    @Autowired
    private lateinit var kafkaAnswerReportService: KafkaAnswerReportService


    /**
     * Получение списка вопросов для заданного теста
     * @param testId Строковое представление UUID теста
     * @param httpServletRequest Объект HttpServletRequest, содержащий заголовки запроса
     * @return ResponseEntity, содержащий список объектов QuestionDto
     * @throws ResponseStatusException, если идентификатор теста не найден или если группа пользователя не имеет доступа к тесту
     */
    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{testId}")
    fun getQuestions(
        @PathVariable testId: String?,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<List<QuestionDto>> {

        // Получение токена авторизации из заголовков запроса, и извлечение из него роли и идентификатора группы
        val token = httpServletRequest.getHeader("Authorization").substring(7)
        val role = JwtUtil.getUserRole(token)
        val groupId = JwtUtil.getGroupId(token)

        // Проверка наличия теста с заданным идентификатором.
        val test = testRepository.findById(UUID.fromString(testId)).getOrNull()
        if (test == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No test with such id found")
        }

        // Создание объекта TestingGroupKey и установка идентификатора теста и группы для поиска в таблице TestingGroup
        val keyToSearch = TestingGroupKey()
        keyToSearch.testId = UUID.fromString(testId)
        keyToSearch.groupId = UUID.fromString(groupId)

        // Проверка, имеет ли пользователь с ролью, отличной от "TEACHER", доступ к тесту через группу
        if (role != "TEACHER" && !testingGroupRepository.existsById(keyToSearch)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Group of token holder doesn't have access to this test"
            )
        }

        // Проверка возмжоности выдачи ответов в зависимости от текущего времени, времени открытия теста и его закрытия
        if (!DateUtil.isCurrentTimeInRange(test.startDate!!, test.deadlineDate!!, "Europe/Moscow")) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Can't send questions because test is closed")
        }

        // Извлечение списка вопросов из репозитория, премешивание порядка и скрытие ответов, если поользователь имеет роль "STUDENT"
        var questionEntities = questionRepository.findAllByTest_TestId(UUID.fromString(testId))
        if (role == "STUDENT") {
            questionEntities = questionService.shuffleTestAnsHideAnswers(questionEntities)
        }

        //  Перевод списка сущностей вопросов в список DTO вопросов
        val resultQuestions = mutableListOf<QuestionDto>()
        for (item in questionEntities) {
            resultQuestions.add(dtoMapper.mapQuestionEntityToDto(item))
        }

        // Возвращение списка вопросов в виде ResponseEntity
        return ResponseEntity.ok(resultQuestions.distinctBy { it.questionId })
    }

    /**
     * Отправку ответов студента на тест с заданным идентификатором.* @param testId Строковое представление UUID теста
     * @param testId Строковое представление UUID теста
     * @param solvedTest Решенный тест
     * @param httpServletRequest Объект HttpServletRequest, содержащий заголовки запроса
     * @return ResponseEntity, содержащий отчет о проверке теста
     * @throws ResponseStatusException если тест не найден, группа не имеет доступа к тесту, тест закрыт или решенный тест не валиден
     */
    @OptIn(ExperimentalStdlibApi::class)
    @PostMapping("{testId}")
    fun postAnswersToACertainTest(
        @PathVariable testId: String?,
        @RequestBody solvedTest: SolvedTestDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<AnswerReport> {

        // Получение токена авторизации из заголовков запроса, и извлечение из него роли и идентификатора группы
        val token = httpServletRequest.getHeader("Authorization").substring(7)
        val studentId = JwtUtil.getUserId(token)
        val role = JwtUtil.getUserRole(token)
        val groupId = JwtUtil.getGroupId(token)

        // Проверка наличия теста с заданным идентификатором.
        val test = testRepository.findById(UUID.fromString(testId)).getOrNull()
        if (test == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No test with such id found")
        }

        // Создание объекта TestingGroupKey и установка идентификатора теста и группы для поиска в таблице TestingGroup
        val keyToSearch = TestingGroupKey()
        keyToSearch.testId = UUID.fromString(testId)
        keyToSearch.groupId = UUID.fromString(groupId)

        // Проверка, имеет ли пользователь с ролью, отличной от "TEACHER", доступ к тесту через группу
        if (role != "TEACHER" && !testingGroupRepository.existsById(keyToSearch)) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Group of token holder doesn't have access to this test"
            )
        }

        // Проверка возмжоности выдачи ответов в зависимости от текущего времени, времени открытия теста и его закрытия
        if (!DateUtil.isCurrentTimeInRange(test.startDate!!, test.deadlineDate!!, "Europe/Moscow")) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Can't receive answers because test is closed")
        }

        // Проверка решенного теста на валидность
        if (!solvedTest.isObjectValid()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad body")
        }

        // Проверка решенного теста на правильность ответов, и формирование отчета проверки
        val correctAnswers = questionRepository.findAllByTest_TestId(UUID.fromString(testId))
        val result = questionService.checkTest(
            studentId,
            solvedTest,
            correctAnswers,
            testRepository.findById(UUID.fromString(testId)).get()
        )

        // Отправка отчета о пройденном тесте в брокер сообщений
        kafkaAnswerReportService.sendAnswerReport(result)

        return ResponseEntity.ok(result)
    }
}