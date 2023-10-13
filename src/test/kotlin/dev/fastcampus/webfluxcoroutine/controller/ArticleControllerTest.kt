package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.model.CreateArticle
import dev.fastcampus.webfluxcoroutine.model.UpdateArticle
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.temporal.ChronoUnit

@SpringBootTest
@ActiveProfiles("test")
class ArticleControllerTest(
    @Autowired private val controller: ArticleController,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val context: ApplicationContext,
) : StringSpec({

     val client = WebTestClient.bindToApplicationContext(context).build()

     beforeTest{
        repository.deleteAll()
     }

    "get" {
        val article = repository.save( Article(title = "title1") )

        val getArticle = client.get().uri("/article/${article.id}")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(Article::class.java)
            .returnResult().responseBody!!

        getArticle.id        shouldBe article.id
        getArticle.title     shouldBe article.title
        getArticle.body      shouldBe article.body
        getArticle.authorId  shouldBe article.authorId
        getArticle.createdAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe article.createdAt?.truncatedTo(ChronoUnit.SECONDS)
        getArticle.updatedAt?.truncatedTo(ChronoUnit.SECONDS) shouldBe article.updatedAt?.truncatedTo(ChronoUnit.SECONDS)
    }

    "getAll" {
        val articles = repository.saveAll(
            listOf(
                Article(title = "title1", body = "body1", authorId = 9999),
                Article(title = "title2", body = "body2", authorId = 9999),
                Article(title = "title3", body = "body3", authorId = 9999),
            )
        )

        val size = client.get().uri("/article")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(List::class.java)
            .returnResult().responseBody?.size ?: 0

        size shouldBe articles.toList().size
    }

    "create" {
        val request = CreateArticle(title = "r2dbc title", body = "r2dbc body", authorId = 9999)

        client.post().uri("/article").accept(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(request.title)
            .jsonPath("body").isEqualTo(request.body ?: "")
            .jsonPath("authorId").isEqualTo(request.authorId ?: 0)
    }

    "update" {
        val updateRequest = UpdateArticle(title = "update title", body = "update body", authorId = 9999)
        val article = repository.save( Article(title = "title1", body = "body1", authorId = 1111) )

        client.put().uri("/article/${article.id}").accept(APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("title").isEqualTo(updateRequest.title ?: "")
            .jsonPath("body").isEqualTo(updateRequest.body ?: "")
            .jsonPath("authorId").isEqualTo(updateRequest.authorId ?: 0)
    }

    "delete" {
        val article = repository.save( Article(title = "title1", body = "body1", authorId = 1111) )

        client.delete().uri("/article/${article.id}").accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        repository.count() shouldBe 0
        repository.existsById(article.id) shouldBe false
    }
})
