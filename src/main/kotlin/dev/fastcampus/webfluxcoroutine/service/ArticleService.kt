package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.exception.ArticleNotFound
import dev.fastcampus.webfluxcoroutine.model.*
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import dev.fastcampus.webfluxcoroutine.util.extension.query
import dev.fastcampus.webfluxcoroutine.util.extension.toLocalDate
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    suspend fun getAll(request: QueryArticle): Flow<Article> {
        val param = HashMap<String, Any>()
        var sql = databaseClient.sql {
            """
            SELECT id, title, body, author_id, created_at, updated_at
            FROM ARTICLE
            WHERE 1=1
            ${
                request.title.query { title ->
                    param["title"] = title.trim().let { "%$it%" }
                    "AND title LIKE :title"
                }
            }
            ${
                request.authorId.query { authorId ->
                    param["authorId"] = authorId
                    "AND author_id in (:authorId)"
                }
            }
            ${
                request.from.query { from -> 
                    param["from"] = from.toLocalDate()
                    "AND created_at >= :from"
                }
            }
            ${
                request.to.query { to -> 
                    param["to"] = to.toLocalDate()
                    "AND created_at <= :to"
                }
            }
            """.trimIndent()
        }

        param.forEach { (key, value) ->
            sql = sql.bind(key, value)
        }

        return sql.map { row ->
            Article(
                id          = row.get("id") as Long,
                title       = row.get("title") as String,
                body        = row.get("body") as String?,
                authorId    = row.get("author_id") as Long,
            ).apply {
                createdAt   = row.get("created_at") as LocalDateTime
                updatedAt   = row.get("updated_at") as LocalDateTime
            }
        }.flow()
    }

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