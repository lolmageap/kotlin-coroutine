package dev.fastcampus.webfluxcoroutine

import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.repository.ArticleRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class WebfluxCoroutineApplicationTests(
    @Autowired private val repository: ArticleRepository,
): StringSpec({
    "contextLoads".config(false) {
        val prev = repository.count()
        repository.save( Article(title = "title1") )
        val curr = repository.count()
        curr shouldBe prev + 1
    }
})