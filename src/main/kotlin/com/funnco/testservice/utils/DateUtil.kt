package com.funnco.testservice.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object DateUtil {

    fun isDateInISO8601(date: String): Boolean {
        return date.matches("^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]{3})?(Z)?$".toRegex())
    }
    fun parseStringToDate(stringDate: String): Date {
        var date = stringDate
        date = date.replace("Z", "")
        date = date.replace("T", " ")
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return try {
            formatter.parse(date)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }

    fun isCurrentTimeInRange(startTime: Date, endTime: Date, timeZone: String): Boolean {
        val zoneId = ZoneId.of(timeZone)
        val start = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.of("UTC"))
        val end = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.of("UTC"))
        val now = LocalDateTime.now(zoneId)
        return now.isAfter(start) && now.isBefore(end)
    }
}