fun main() {
    inc(11)
}

fun inc(n: Int?) : Int? {
    return if(n == null) null 
    else n + 1 // <=> Integer.valueOf(n.intValue() + 1)
}
