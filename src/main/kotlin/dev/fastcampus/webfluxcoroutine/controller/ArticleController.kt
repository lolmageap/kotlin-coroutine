package dev.fastcampus.webfluxcoroutine.controller

import dev.fastcampus.webfluxcoroutine.model.Article
import dev.fastcampus.webfluxcoroutine.model.CreateArticle
import dev.fastcampus.webfluxcoroutine.model.QueryArticle
import dev.fastcampus.webfluxcoroutine.model.UpdateArticle
import dev.fastcampus.webfluxcoroutine.service.ArticleService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): Article {
        return articleService.get(id)
    }

    @GetMapping
    suspend fun getAll(request: QueryArticle): Flow<Article> {
        return articleService.getAll(request)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody createArticle: CreateArticle) {
        articleService.create(createArticle)
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: Long, @RequestBody updateArticle: UpdateArticle) {
         articleService.update(id, updateArticle)
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
         articleService.delete(id)
    }

}