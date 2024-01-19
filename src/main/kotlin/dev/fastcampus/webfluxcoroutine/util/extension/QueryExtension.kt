package dev.fastcampus.webfluxcoroutine.util.extension

fun <T> T?.query(f: (T) -> String) =
    when {
        this == null -> ""
        this is String && this.isBlank() -> ""
        this is Collection<*> && this.isEmpty() -> ""
        this is Array<*> && this.isEmpty() -> ""
        else -> f.invoke(this)
    }
