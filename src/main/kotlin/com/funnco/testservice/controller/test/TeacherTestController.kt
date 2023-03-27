package com.funnco.testservice.controller.test

import com.funnco.testservice.dto.DtoMapper
import com.funnco.testservice.dto.request.NewQuestionDto
import com.funnco.testservice.dto.request.NewTestDto
import com.funnco.testservice.dto.universal.QuestionDto
import com.funnco.testservice.dto.response.ResponseTestDto
import com.funnco.testservice.entity.QuestionEntity
import com.funnco.testservice.entity.TestingGroupEntity
import com.funnco.testservice.entity.composite_key.TestingGroupKey
import com.funnco.testservice.repository.QuestionRepository
import com.funnco.testservice.repository.TestRepository
import com.funnco.testservice.repository.TestingGroupRepository
import com.funnco.testservice.service.UserRestService
import com.funnco.testservice.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/v2/test/teacher")
class TeacherTestController {

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
     * Экземпляр UserRestService, используемый для доступа к пользователей из другого сервиса
     */
    @Autowired
    private lateinit var userRestService: UserRestService

    /**
     * Экземпляр TestingGroupRepository, используемый для обработки данных о группах, которым разрешено проходить определенные тесты
     */
    @Autowired
    private lateinit var testingGroupRepository: TestingGroupRepository

    /**
     * Экземпляр QuestionService, используемый для обработки даанных о вопросах
     */
    @Autowired
    private lateinit var questionRepository: QuestionRepository

    /**
    Получение теста по его идентификатору.
     * @param testId идентификатор теста.
     * @return HTTP-ответ с телом, содержащим объект DTO теста.
     * @throws ResponseStatusException исключение, возникающее в случае, если теста с запрошенным идентификатором не существует
     */
    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/get/{testId}")
    fun getTest(
        @PathVariable(name = "testId") testId: String
    ): ResponseEntity<ResponseTestDto> {
        val test = testRepository.findById(UUID.fromString(testId)).getOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No test with such id found")

        return ResponseEntity.ok(dtoMapper.mapTestEntityToResponseDto(test))
    }

    /**
    Метод для создания нового теста.
     * @param newTest Объект класса NewTestDto, содержащий данные нового теста.
     * @param requestServlet Объект класса HttpServletRequest, содержащий информацию о запросе.
     * @return Объект класса ResponseEntity<ResponseTestDto>, содержащий информацию о созданном тесте.
     * @throws ResponseStatusException исключение, выбрасываемое при некорректных входных данных (например, если название теста некорректное,
     * или если список разрешенных групп пуст или содержит дубликаты), либо при отсутствии прав доступа.
     */
    @PostMapping("/add")
    fun addTest(
        @RequestBody newTest: NewTestDto,
        requestServlet: HttpServletRequest
    ): ResponseEntity<ResponseTestDto> {

        // Проверка на корректность входных данных
        if (!newTest.isObjectValid()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field in body is bad")
        }
        if (newTest.listOfPermittedGroups!!.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "List of permitted groups cannot be empty")
        }
        if (newTest.listOfPermittedGroups.size != newTest.listOfPermittedGroups.distinct().size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "List of permitted groups cannot contain duplicates")
        }

        // Проверка прав доступа
        if (!userRestService.validateGroupsByRest(
                newTest.listOfPermittedGroups,
                requestServlet.getHeader("Authorization")
            )
        ) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one id from groups is bad")
        }

        // Получение id пользователя из токена
        val token = requestServlet.getHeader("Authorization").substring(7)
        val userId = JwtUtil.getUserId(token)

        // Создание нового теста
        val testEntity = testRepository.save(dtoMapper.mapNewTestDtoToEntity(newTest, userId))

        // Создание списка разрешенных групп для нового теста
        val listOfPermittedGroups = mutableListOf<TestingGroupEntity>()
        for (item: String in newTest.listOfPermittedGroups) {
            val tempKey = TestingGroupKey()
            tempKey.testId = testEntity.testId!!
            tempKey.groupId = UUID.fromString(item)
            val tempTestGroup = TestingGroupEntity()
            tempTestGroup.id = tempKey
            listOfPermittedGroups.add(tempTestGroup)
        }

        // Сохранение списка разрешенных групп
        testingGroupRepository.saveAll(listOfPermittedGroups)

        // Возвращение информации о созданном тесте
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.mapTestEntityToResponseDto(testEntity))
    }

    /**
    Получение всех тестов.
     * @param httpServletRequest Объект HttpServletRequest, содержащий заголовки запроса
     * @return Список бъектов класса ResponseEntity<ResponseTestDto>, содержащих информацию о тестах.
     */
    @GetMapping("/get/all")
    fun getAllTests(httpServletRequest: HttpServletRequest): ResponseEntity<List<ResponseTestDto>> {
        val token = httpServletRequest.getHeader("Authorization").substring(7)
        val allAuthoredTests = testRepository.findTestEntitiesByAuthorId(UUID.fromString(JwtUtil.getUserId(token)))
        val resultList = mutableListOf<ResponseTestDto>()
        for (entity in allAuthoredTests) {
            resultList.add(dtoMapper.mapTestEntityToResponseDto(entity))
        }
        return ResponseEntity.ok(resultList)
    }

    /**

     * Добавление вопросов в тест с указанным идентификатором.
     * @param testId идентификатор теста, в который добавляются вопросы.
     * @param questions список новых вопросов.
     * @return объект ResponseEntity с кодом статуса CREATED и списком созданных вопросов в теле ответа.
     * @throws ResponseStatusException если тест с указанным идентификатором не найден, если хотя бы один из переданных вопросов некорректен,
     * если номер вопроса дублируется с уже существующим вопросом в тесте, или если переданный тип вопроса не найден.
     */
    @OptIn(ExperimentalStdlibApi::class)
    @PostMapping("/add/{testId}/questions")
    fun addQuestions(
        @PathVariable(name = "testId") testId: String,
        @RequestBody questions: List<NewQuestionDto>
    ): ResponseEntity<List<QuestionDto>> {
        // Получаем сущность теста по его id
        val testEntity = testRepository.findById(UUID.fromString(testId)).getOrNull()

        // Если такой тест не существует, выбрасываем исключение с кодом 404
        if (testEntity == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No test with such id found")
        }

        // Проверяем, что все переданные вопросы валидны, иначе выбрасываем исключение с кодом 400
        if (questions.any { !it.isObjectValid() }) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one of questions is bad")
        }

        // Создаем пустой список сущностей вопросов, которые будут добавлены в базу данных
        var resultQuestionEntities = mutableListOf<QuestionEntity>()

        // Проходимся по каждому переданному вопросу
        for (newQuestion in questions) {
            var newQuestionEntity: QuestionEntity
            try {
                // Преобразуем вопрос из Dto в Entity и сохраняем его в переменную newQuestionEntity
                newQuestionEntity = dtoMapper.mapNewQuestionDtoToEntity(newQuestion, testId)
            } catch (e: Exception) {
                // Если переданный вопрос невалидный, выбрасываем исключение с кодом 404
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "At least one of passed question types is bad")
            }
            // Проверяем, что у вопросов нет дубликатов по номеру, иначе выбрасываем исключение с кодом 400
            if (resultQuestionEntities.any { question -> question.number == newQuestionEntity.number }) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one question has duplicate number")
            }
            // Добавляем сущность вопроса в список
            resultQuestionEntities.add(newQuestionEntity)
        }

        // Получаем все существующие вопросы для данного теста
        val existingQuestionsForTest = questionRepository.findAllByTest_TestId(UUID.fromString(testId))

        // Проверяем, что вновь добавляемые вопросы не дублируют уже существующие вопросы
        for (existingQuestion in existingQuestionsForTest) {
            if (resultQuestionEntities.any { newQuestion -> newQuestion.number == existingQuestion.number }) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one question has duplicate number with already existing question"
                )
            }
        }

        // Сохраняем все вновь созданные сущности вопросов в базу данных
        resultQuestionEntities = questionRepository.saveAll(resultQuestionEntities).toMutableList()

        // Преобразуем каждую сущность вопроса в Dto и добавляем его в список возвращаемых значений
        val returnList = mutableListOf<QuestionDto>()
        for (item in resultQuestionEntities) {
            returnList.add(dtoMapper.mapQuestionEntityToDto(item))
        }

        // Возвращаем список вопросов с кодом 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(returnList)
    }

    @PutMapping("/{testId}")
    fun changeTest(
        @PathVariable(name = "testId") testId: String
    ) {
        // WIP
        // Не реализовано, т.к. это будет связано с сервисом статистики, которого еще нет
    }

    @PutMapping("/{testId}/questions")
    fun changeTestQuestions(
        @PathVariable(name = "testId") testId: String
    ) {
        // WIP
        // Не реализовано, т.к. это будет связано с сервисом статистики, которого еще нет
    }
}