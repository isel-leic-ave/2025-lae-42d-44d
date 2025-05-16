package pt.isel

fun <T, R> Iterable<T>.eagerMap(transform: (T) -> R): List<R> {
    val destination = mutableListOf<R>()
    for (item in this)
        destination.add(transform(item))
    return destination
}

fun <T> Iterable<T>.eagerFilter(predicate: (T) -> Boolean): Iterable<T> {
    val destination = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            destination.add(item)
        }
    }
    return destination
}

fun <T, R> Sequence<T>.suspMap(transform: (T) -> R) = sequence {
    for (item in this@suspMap)
        yield(transform(item))
}

fun <T> Sequence<T>.suspFilter(predicate: (T) -> Boolean) = sequence {
    for (item in this@suspFilter) {
        if (predicate(item)) {
            yield(item)
        }
    }
}

fun <T> Iterable<T>.eagerDistinct(): Iterable<T> {
    val destination = mutableSetOf<T>()
    for (item in this) {
        destination.add(item)
    }
    return destination
}

fun <T, R> Sequence<T>.lazyMap(transform: (T) -> R): Sequence<R> =
    object : Sequence<R> {
        override fun iterator(): Iterator<R> =
            object : Iterator<R> {
                val iter = this@lazyMap.iterator()

                override fun hasNext() = iter.hasNext()

                override fun next() = transform(iter.next())
            }
    }

/**
 * DOES NOT SUPPORT Sequences with null elements
 */
fun <T> Sequence<T>.lazyDistinct(): Sequence<T> {
    TODO()
}

fun <T> Sequence<T>.suspDistinct(): Sequence<T> {
    TODO()
}

public fun <T, R, V> Sequence<T>.suspZip(other: Sequence<R>, transform: (a: T, b: R) -> V): Sequence<V> {
    TODO()
}

