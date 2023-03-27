package com.funnco.testservice.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import com.funnco.testservice.dto.request.SolvedTestDto
import com.funnco.testservice.entity.QuestionEntity
import com.funnco.testservice.entity.TestEntity
import com.funnco.testservice.model.AnswerReport
import com.funnco.testservice.utils.DateUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*


@Service
class QuestionService {

    private val logger = LoggerFactory.getLogger(this::class.java.simpleName)

    /**
     * Метод, проверяющий полученный тест студента
     * @param studentId ID студента решившего тест
     * @param solvedTest Решенный студентом тест
     * @param correctAnswers Список ответов из БД
     * @param test Объект теста из базы данных, для получения критериев оценки
     */
    fun checkTest(
        studentId: String,
        solvedTest: SolvedTestDto,
        correctAnswers: List<QuestionEntity>,
        test: TestEntity
    ): AnswerReport {

        // Время, когда тест считается решенным, и начинается его проверка
        val solveDate = Date(System.currentTimeMillis())

        // Проверка ответов на вопросы
        val checkList = mutableListOf<Boolean>()
        val sortedCorrectAnswers = correctAnswers.sortedBy { it.number }
        for (i in sortedCorrectAnswers.indices) {
            checkList.add(
                checkQuestion(
                    solvedTest.answers!!.find {
                        it.questionId!! == correctAnswers[i].questionId!!.toString()
                    }?.body, correctAnswers[i].body!!
                )
            )
        }

        // Подсчет максимально возможного веса оценок
        var totalPossibleWeight = 0
        sortedCorrectAnswers.forEach { totalPossibleWeight += it.weight!! }

        // Подсчет полученных баллов
        var actualWeight = 0
        for (i in checkList.indices) {
            if (checkList[i]) {
                actualWeight += sortedCorrectAnswers[i].weight!!
            }
        }

        // Определение оценки
        val mark = when (actualWeight) {
            in test.criteriaExcellent!!..totalPossibleWeight -> 5
            in test.criteriaGood!! until test.criteriaExcellent!! -> 4
            in test.criteriaPass!! until test.criteriaGood!! -> 3
            else -> 2
        }

        // Формирование отчета о проверенном тесте
        return AnswerReport(
            studentId,
            test.testId.toString(),
            checkList,
            mark,
            solveDate,
            DateUtil.parseStringToDate(solvedTest.finishSolvingDateTime!!).time - DateUtil.parseStringToDate(solvedTest.startSolvingDateTime!!).time
        )
    }

    /**
     * Метод, отвечающий за проверку 1 вопроса. Здесь проходит базовая валидация тела ответа,
     * и определяется типа вопроса: с массивом ответов, или с 1 ответом.
     * @param studentAnswer JSON решеного вопроса студента
     * @param correctAnswer JSON решеного вопроса из базы данных
     */
    private fun checkQuestion(studentAnswer: JsonNode?, correctAnswer: JsonNode): Boolean {
        // Проверка, прислал ли студент ответ
        if (studentAnswer == null) {
            logger.info(studentAnswer.toString())
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one of the answers has bad structure (no \"body\" field found)"
            )
        }

        // Определение типа вопроса и выбор соответствующего метода проверки
        val isArrayAnswer = correctAnswer.has("arrayAnswer")
        return if (isArrayAnswer) checkArrayAnswers(studentAnswer, correctAnswer)
        else checkSingleAnswer(studentAnswer.get("singleAnswer"), correctAnswer.get("singleAnswer"))
    }

    /**
     * Метод, отвечающий за проверку массива ответов к вопросу.
     * @param studentAnswer JSON решеного вопроса студента
     * @param correctAnswer JSON решеного вопроса из базы данных
     */
    private fun checkArrayAnswers(studentAnswer: JsonNode, correctAnswer: JsonNode): Boolean {
        // Проверка, ялвяется ли ответ массивом данных
        if (!studentAnswer.get("arrayAnswer").isArray) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one of the answers has bad structure (expected array, found object)"
            )
        }

        // Перевод JSON в списки проверяемых и правильных ответов
        val expectedAnswers = (correctAnswer.get("arrayAnswer") as ArrayNode).toList()
        val actualAnswers = (studentAnswer.get("arrayAnswer") as ArrayNode).toList()

        // Проверка на соотвествие размеров списков. Они обязаны быть одинаковыми
        if (expectedAnswers.size != actualAnswers.size) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one of the answers has bad structure (array sizes doesn't match)"
            )
        }

        // Проверка каждого ответа
        // Считается, что если хотябы один ответ не соответсвует правильному, то весь вопрос решен неправильно
        var areAllAnswersCorrect = true
        for (i in expectedAnswers.indices) {
            if (!checkSingleAnswer(actualAnswers[i], expectedAnswers[i])) {
                areAllAnswersCorrect = false
                break
            }
        }
        return areAllAnswersCorrect
    }

    /**
     * Метод, отвечающий за проверку ответов на 1 вопрос.
     * @param studentAnswer JSON решеного вопроса студента
     * @param correctAnswer JSON решеного вопроса из базы данных
     */
    private fun checkSingleAnswer(studentAnswer: JsonNode, correctAnswer: JsonNode): Boolean {
        // Проверка соотстветсвия параметра сontent. Это необходимо, т.к. в некоторых типах вопросов, content может изменяться
        // Поле необязательно, поэтому, если его нет в ответе, оно автоматически считается верным
        val contentCheck: Boolean = if (correctAnswer.has("content")) {
            if (studentAnswer.has("content")) {
                compareTwoFields(correctAnswer.get("content"), studentAnswer.get("content"))
            } else {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one of the answers has bad structure (no \"content\" field found)"
                )
            }
        } else {
            true
        }

        // Наличие поля answer обазятельно, поэтому в correctAnswer поле не проверяется
        val answerCheck: Boolean = if (studentAnswer.has("answer")) {
            compareTwoFields(correctAnswer.get("answer"), studentAnswer.get("answer"))
        } else {
            logger.info(studentAnswer.toString())
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one of the answers has bad structure (no \"answer\" field found)"
            )
        }

        // Если обе проверки успешны, то ответ считается верным
        return contentCheck && answerCheck
    }

    /**
     * Метод, отвечающий за сравнение 2 JSON полей.
     * @param field1 JSON с которым идет сравниение
     * @param field2 JSON который сравнивают с другим
     */
    private fun compareTwoFields(field1: JsonNode, field2: JsonNode): Boolean {

        // Проверка типов данных на соответствие
        if (field1.nodeType != field2.nodeType) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "At least one of the answers has bad structure (type compare)"
            )
        }

        // Сравнение значений, на основе типа даныных. Если полученный тип данных не число, текст или логическое значение,
        // то полученный ответ считается неверным, т.к. в вопросах могут использовтаься только перечисленные типы данных
        return when (field1.nodeType) {
            JsonNodeType.BOOLEAN -> {
                field1.asBoolean() == field2.asBoolean()
            }

            JsonNodeType.NUMBER -> {
                field1.asInt() == field2.asInt()
            }

            JsonNodeType.STRING -> {
                field1.asText() == field2.asText()
            }

            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected type of answer in question")
            }
        }
    }

    /**
     * Метод перемешивает список объектов QuestionEntity, переданный в качестве параметра, и скрывает ответы на вопросы в зависимости от их типа.
     * @param listToChange список объектов QuestionEntity, который нужно перемешать и скрыть ответы.
     * @return перемешанный список объектов QuestionEntity, с ответами на вопросы скрытыми в соответствии с их типом.
     */
    fun shuffleTestAnsHideAnswers(listToChange: List<QuestionEntity>): List<QuestionEntity> {
        // Создание нового списка и перемешивание входного списка
        val resultList = listToChange.shuffled()

        // Обработка каждого вопроса в списке
        for (entity in resultList) {
            val answerBody = entity.body!!

            // Определение типа вопроса
            if (answerBody.has("singleAnswer")) {
                hideAnswersForQuestion(answerBody.get("singleAnswer"))
            } else {

                // Выделение вопросов, связанных с установлением соотношения, т.к. из обработка отличается от остальных типов
                if (!entity.type!!.name!!.contains("order")) {
                    (answerBody.get("arrayAnswer") as ArrayNode).forEach {
                        hideAnswersForQuestion(it)
                    }
                } else {
                    hideAnswersForOrderBasedQuestions(answerBody.get("arrayAnswer") as ArrayNode)
                }
            }
            entity.body = answerBody
        }

        return resultList
    }

    /**
     * Метод скрывает ответы на вопросы, переданные в качестве параметра в формате JsonNode, в соответствии с типом ответа на вопрос.
     * @param question вопрос в формате JsonNode, ответ на который нужно скрыть.
     * @throws ResponseStatusException если тип ответа на вопрос не является ожидаемым.
     */
    private fun hideAnswersForQuestion(question: JsonNode) {
        when (question.get("answer").nodeType) {
            JsonNodeType.BOOLEAN -> (question as ObjectNode).put("answer", false)
            JsonNodeType.NUMBER, JsonNodeType.STRING -> (question as ObjectNode).putNull("answer")
            else -> {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected type of answer in question")
            }
        }
    }

    /**
     * Метод перемешивает ответы на вопросы, переданные в качестве параметра в формате ArrayNode, для вопросов на соотношение
     * @param answers ответы на вопросы в формате ArrayNode, которые нужно перемешать.
     */
    private fun hideAnswersForOrderBasedQuestions(answers: ArrayNode) {

        // Создание списков для полей "content" и "answer"
        val firstList = mutableListOf<String>()
        val secondList = mutableListOf<String>()

        // Заполнение данных списков на основе ответов
        for (node in answers) {
            firstList.add(node.get("content").asText())
            secondList.add(node.get("answer").asText())
        }

        // Перемешивание списков
        firstList.shuffle()
        secondList.shuffle()

        // Замена существующих ответов, на перемешанные
        for (i in firstList.indices) {
            (answers[i] as ObjectNode).put("content", firstList[i])
            (answers[i] as ObjectNode).put("answer", secondList[i])
        }
    }
}