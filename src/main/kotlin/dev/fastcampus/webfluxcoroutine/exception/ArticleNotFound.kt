package dev.fastcampus.webfluxcoroutine.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ArticleNotFound(message: String?): Throwable(message)