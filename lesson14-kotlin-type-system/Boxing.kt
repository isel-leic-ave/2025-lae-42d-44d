fun main() {
    aux(7) // Boxing
}
fun aux(n: Int?) {
    check(n != null)
    println(n + 3) // Unboxing
}