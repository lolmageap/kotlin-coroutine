package dev.fastcampus.webfluxcoroutine.model

data class CreateArticle(
    val title: String,
    val body: String? = null,
    val authorId: Long? = null,
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

data class QueryArticle(
    val title: String?,
    val authorId: List<Long>,

//    @DateString
    val from: String?,

//    @DateString
    val to: String?,
)