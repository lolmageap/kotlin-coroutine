package dev.fastcampus.webfluxcoroutine.repository

import dev.fastcampus.webfluxcoroutine.model.Article
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ArticleRepository: R2dbcRepository<Article, Long> {
    suspend fun findAllByTitleContains(title: String): Flux<Article>
}