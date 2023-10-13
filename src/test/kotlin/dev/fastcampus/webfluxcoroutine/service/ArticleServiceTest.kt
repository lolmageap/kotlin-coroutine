package dev.fastcampus.webfluxcoroutine.service

import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.model.CreateArticle
import dev.fastcampus.webfluxcoroutine.model.UpdateArticle
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@SpringBootTest
@ActiveProfiles("test")
class ArticleServiceTest(
    @Autowired private val service: ArticleService,
    @Autowired private val repository: ArticleRepository,
    @Autowired private val rxtx: TransactionalOperator,
): StringSpec({

//    beforeTest {
//        repository.deleteAll()
//    }

    "create" {
        rxtx.rollback {
            val article = service.create(CreateArticle(title = "title1"))
            val findArticle = repository.findById(article.id)!!

            findArticle.id shouldBe article.id
            findArticle.title shouldBe article.title
            findArticle.body shouldBe article.body
            findArticle.authorId shouldBe article.authorId
            findArticle.createdAt shouldNotBe null
            findArticle.updatedAt shouldNotBe null
        }
    }

    "get" {
        rxtx.rollback {
            val article = repository.save(Article(title = "title1"))
            val findArticle = service.get(article.id)

            findArticle.title shouldBe article.title
            findArticle.createdAt shouldNotBe null
            findArticle.updatedAt shouldNotBe null

        }
    }

    "getAll" {
        rxtx.rollback {
            repository.saveAll(
                listOf(
                    Article(title = "title1", body = "body1", authorId = 9999),
                    Article(title = "title2", body = "body2", authorId = 9999),
                    Article(title = "title3", body = "body3", authorId = 9999),
                )
            )

            val findArticle = service.getAll()
            val findArticleWithTitle = service.getAll("2")

            findArticle.toList().size shouldBe 3
            findArticleWithTitle.toList().size shouldBe 1
        }
    }

    "update" {
        rxtx.rollback {
            val updateRequest = UpdateArticle(title = "update title", body = "update body", authorId = 1111)
            val article = repository.save(Article(title = "title1"))
            val updateArticle = service.update(article.id, updateRequest)

            updateArticle.title shouldBe updateRequest.title
            updateArticle.body shouldBe updateRequest.body
            updateArticle.authorId shouldBe updateRequest.authorId
        }
    }

    "delete" {
        rxtx.rollback {
            val article = repository.save(Article(title = "title1"))
            service.delete(article.id)

            repository.count() shouldBe 0
        }
    }
})

suspend fun <T> TransactionalOperator.rollback(f: suspend (ReactiveTransaction) -> T) {
    return this.executeAndAwait { tx ->
        tx.setRollbackOnly()
    }
}