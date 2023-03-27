package com.funnco.testservice.controller.subject

import com.funnco.testservice.dto.DtoMapper
import com.funnco.testservice.dto.request.NewSubjectDto
import com.funnco.testservice.dto.universal.SubjectDto
import com.funnco.testservice.entity.SubjectEntity
import com.funnco.testservice.repository.SubjectRepository
import com.funnco.testservice.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
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
import javax.management.relation.Role
import kotlin.jvm.optionals.getOrNull


/**
 * Контроллер, отвечающий за работу с предметами.
 */
@RestController
@RequestMapping(("/api/v2/subject"))
class SubjectController {

    /**
     * Экземпляр SubjectRepository, используемый для доступа к сеществующим предметам и из добавления.
     */
    @Autowired
    private lateinit var subjectRepository: SubjectRepository

    /**
     * Экземпляр DtoMapper, используемый для перевода сущностей в DTO или в обратном порядке.
     */
    @Autowired
    private lateinit var dtoMapper: DtoMapper

    /**
     * Получение списка всех предметов.
     * @return Список объектов SubjectDto, представляющих собой информацию о предметах.
     */
    @GetMapping("/all")
    fun getAllSubjects(): ResponseEntity<List<SubjectDto>> {
        val subjects = subjectRepository.findAll()
        val resultList = mutableListOf<SubjectDto>()
        for (subject: SubjectEntity in subjects) {
            resultList.add(dtoMapper.mapSubjectEntityToDto(subject))
        }
        return ResponseEntity.ok(resultList)
    }

    /**
     * Получение предмета по его id.
     * @return Объект SubjectDto, представляющий собой информацию о предмете.
     */
    @OptIn(ExperimentalStdlibApi::class)
    @GetMapping("/{subjectId}")
    fun getSubject(
        @PathVariable(name = "subjectId") subjectId: String?
    ): ResponseEntity<SubjectDto> {
        val entity = subjectRepository.findById(UUID.fromString(subjectId!!)).getOrNull()
        if (entity == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No subject with such id found")
        }

        return ResponseEntity.ok(dtoMapper.mapSubjectEntityToDto(entity))
    }

    /**
     * Добавление нового предмета.
     * @param dto Объект NewSubjectDto в формате JSON, содержащий данные о новом предмете.
     * @param httpServletRequest Объект HttpServletRequest, содержащий заголовки запроса
     * @return Объект SubjectDto с данными о созданном предмете, завернутый в ResponseEntity.
     * @throws ResponseStatusException Если переданные данные некорректны или предмет с таким именем уже существует.
     */
    @PostMapping("/add")
    fun addSubject(
        @RequestBody dto: NewSubjectDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<SubjectDto> {

        validateAccess(httpServletRequest)

        if (!dto.isObjectValid()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field in body is incorrect")
        }
        if (subjectRepository.existsByName(dto.name!!)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Subject with such name already exists")
        }

        val entity = subjectRepository.save(dtoMapper.mapSubjectDtoToEntity(dto))
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.mapSubjectEntityToDto(entity))
    }

    /**

     * Изменение предмета с заданным id.
     * @param subjectId id предмета, который необходимо изменить.
     * @param dto объект класса NewSubjectDto, содержащий данные для изменения предмета.
     * @param httpServletRequest Объект HttpServletRequest, содержащий заголовки запроса.
     * @throws ResponseStatusException если хотя бы одно поле в объекте NewSubjectDto невалидно или предмет с заданным id не найден.
     * @throws ResponseStatusException если у пользователя нет прав на изменение предметов.
     * @return объект класса ResponseEntity с кодом состояния CREATED и объектом класса SubjectDto,  представляющим измененный предмет в случае успешного изменения.
     */
    @OptIn(ExperimentalStdlibApi::class)
    @PutMapping("/{subjectId}")
    fun change(
        @PathVariable(name = "subjectId") subjectId: String?,
        @RequestBody dto: NewSubjectDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<SubjectDto> {

        validateAccess(httpServletRequest)

        if (!dto.isObjectValid()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field in body is incorrect")
        }
        val entity = subjectRepository.findById(UUID.fromString(subjectId!!)).getOrNull()
        if (entity == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No subject with such id found")
        }

        subjectRepository.save(entity)
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.mapSubjectEntityToDto(entity))
    }

    /**
     * Проверка, есть ли у пользователя права на изменение предметов.
     * @param httpServletRequest запрос http, содержащий заголовок Authorization для аутентификации пользователя
     * @throws ResponseStatusException если у пользователя нет прав на изменение предметов, выдается исключение с кодом статуса 403
     */
    private fun validateAccess(httpServletRequest: HttpServletRequest) {
        val token = httpServletRequest.getHeader("Authorization").substring(7)
        if (JwtUtil.getUserRole(token) != "TEACHER") {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No privilege to change subjects")
        }
    }
}