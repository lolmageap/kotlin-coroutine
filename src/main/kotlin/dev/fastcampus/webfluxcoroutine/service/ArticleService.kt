package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.exception.ArticleNotFound
import dev.fastcampus.webfluxcoroutine.model.*
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val databaseClient: DatabaseClient,
) {

    suspend fun create(createArticle: CreateArticle) = articleRepository.save(createArticle.toEntity())

    suspend fun get(id: Long): Article = articleRepository.findById(id)
        ?: throw ArticleNotFound("No article found (id: $id)")

//    suspend fun getAll(title: String? = null) =
//        if ( title.isNullOrBlank() ) articleRepository.findAll()
//        else articleRepository.findAllByTitleContains(title)

    suspend fun update(id: Long, updateArticle: UpdateArticle): Article {
        val article = articleRepository.findById(id)
            ?: throw ArticleNotFound("No article found (id: $id)")

        return articleRepository.save(article).apply {
            updateArticle.title?.let { title = it }
            updateArticle.body?.let { body = it }
            updateArticle.authorId?.let { authorId = it }
        }
    }

    suspend fun getAll(request: QueryArticle) {
        val param = HashMap<String, Any>()
        val sql = databaseClient.sql {
            """
            SELECT * FROM ARTICLE
            WHERE 1=1
            ${
                request.title.query { title ->
                    param["title"] = title.trim().let { "%$it%" }
                    "AND title LIKE :title"
                }
            }
            ${
                request.authorId.query {
                    param["authorId"] = it
                    "AND author_id in (:authorId)"
                }
            }
            """.trimIndent()
        }
    }

    suspend fun delete(id: Long) = articleRepository.deleteById(id)

}

fun <T> T?.query(f: (T) -> String) =
    when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }