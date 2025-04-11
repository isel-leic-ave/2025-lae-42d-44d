package pt.isel

interface Mapper<T, R> {
    fun mapFrom(src: T): R
}
