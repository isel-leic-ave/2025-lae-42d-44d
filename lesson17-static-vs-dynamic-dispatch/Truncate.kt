open class Truncate(protected val nr: Double) {
    open fun cut(): Double = nr.toInt().toDouble()

    open fun cut(decimals: Int): Double {
        var decimals = decimals
        var d = 1
        while (decimals-- > 0) { d *= 10 }
        return (nr * d).toInt().toDouble() / d
    }
}

class Round(nr: Double) : Truncate(nr) {
    override fun cut(): Double = ((nr + 0.5).toInt()).toDouble()

    override fun cut(decimals: Int): Double {
        var decimals = decimals
        var d = 1
        while (decimals-- > 0) { d *= 10 }
        return (nr * d + 0.5).toInt() / d.toDouble()
    }
}
