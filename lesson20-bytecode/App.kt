import java.io.PrintStream
import kotlin.math.sqrt

class A {
    fun dummy(x: Float, y: Float) : Float {
        return x + y
    }
}


fun modulus(x: Float, y: Float) : Float {
    val res = sqrt(x*x + y*y)
    return res
}

fun calculateNetBalance(
    balance: Int,
    tax: Float,
    interest: Float,
    income: Int,
    expense: Float
): Float {
    return balance - balance * tax + balance * interest + income - expense
}

class Person(val name: String) {
    fun print(label: String) {
        // this is local variable 0
        val myName = this.name
        // Label is local variable 1
        println(label + ": " + myName)
    }
}

class Student()

fun main() {
    val st = Student()
    val p = Person("Maria")

    // bytecode?
    //    getstatic
    //    astore_2
    //    aload_2 // push ref out
    //    aload_1  // push p -> Maria
    //    invokevirtual       // println():(Ljava/lang/String;)V
    val output = System.out
    output.println(p)
}