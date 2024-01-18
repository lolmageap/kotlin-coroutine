package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.exception.ArticleNotFound
import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.model.CreateArticle
import dev.fastcampus.webfluxcoroutine.model.UpdateArticle
import dev.fastcampus.webfluxcoroutine.model.toEntity
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
) {

    suspend fun create(createArticle: CreateArticle) = articleRepository.save( createArticle.toEntity() )

    suspend fun get(id: Long) : Article = articleRepository.findById(id)
        ?: throw ArticleNotFound("No article found (id: $id)")

    suspend fun getAll(title: String? = null) =
        if ( title.isNullOrBlank() ) articleRepository.findAll()
        else articleRepository.findAllByTitleContains(title)

    suspend fun update(id: Long, updateArticle: UpdateArticle): Article {
        val article = articleRepository.findById(id)
            ?: throw ArticleNotFound("No article found (id: $id)")

        return articleRepository.save(article).apply {
            updateArticle.title?.let { title = it }
            updateArticle.body?.let { body = it }
            updateArticle.authorId?.let { authorId = it }
        }
    }

    suspend fun delete(id: Long) = articleRepository.deleteById(id)

}