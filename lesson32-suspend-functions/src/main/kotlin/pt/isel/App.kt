package pt.isel

import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() {
    runBlocking {
        val result = fetchSuspend("https://github.com")
        println(
            result
                .substringAfter("<title>")
                .substringBefore("</title>"),
        )

        val fetchCps: (String, Continuation<String>) -> Any = ::fetchSuspend as (String, Continuation<String>) -> Any
        val coroutineStatus = fetchCps(
            "https://github.com",
            object : Continuation<String> {
                override val context: CoroutineContext
                    get() = EmptyCoroutineContext

                override fun resumeWith(result: Result<String>) {
                    val body = result.getOrThrow()
                    println(
                        body
                            .substringAfter("<title>")
                            .substringBefore("</title>"),
                    )
                }
            },
        )
    }
    sleep(1000)
}
