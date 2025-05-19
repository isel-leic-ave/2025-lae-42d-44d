package pt.isel

import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    runBlocking {
        val result = fetchSuspend("https://github.com")
        val title =
            result
                .substringAfter("<title>")
                .substringBefore("</title>")
        println(title)
    }
}
