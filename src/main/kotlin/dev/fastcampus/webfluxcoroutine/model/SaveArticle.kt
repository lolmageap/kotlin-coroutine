package dev.fastcampus.webfluxcoroutine.model

data class CreateArticle(
    val title: String,
    val body: String?,
    val authorId: Long?,
)

fun CreateArticle.toEntity() : Article =
    Article(
        title = title,
        body = body,
        authorId = authorId,
    )

data class UpdateArticle(
    val title: String?,
    val body: String?,
    val authorId: Long?,
)

