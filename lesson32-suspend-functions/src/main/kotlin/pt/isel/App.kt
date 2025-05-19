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

        val fetchCps = ::fetchSuspend as ((url: String, Continuation<String>) -> Any)
        fetchCps("https://github.com", object : Continuation<String> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(result: Result<String>) {
                val body = result.getOrThrow()
                val title =
                    body
                        .substringAfter("<title>")
                        .substringBefore("</title>")
                println(title)
            }
        })
        // sleep(1000)
    }
}
