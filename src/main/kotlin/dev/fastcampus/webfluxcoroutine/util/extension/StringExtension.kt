package dev.fastcampus.webfluxcoroutine.util.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(format: String = "yyyyMMdd") =
    LocalDate.parse(
        this.filter { it.isDigit() },
        DateTimeFormatter.ofPattern(format)
    )