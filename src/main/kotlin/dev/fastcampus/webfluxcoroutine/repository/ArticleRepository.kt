//package dev.fastcampus.webfluxcoroutine.repository
//
//import dev.fastcampus.webfluxcoroutine.model.Article
//import kotlinx.coroutines.flow.Flow
//import org.springframework.data.repository.kotlin.CoroutineCrudRepository
//
//interface ArticleRepository: CoroutineCrudRepository<Article, Long> {
//    suspend fun findAllByTitleContains(title: String): Flow<Article>
//}