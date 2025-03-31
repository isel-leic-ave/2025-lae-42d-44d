open class Person(val name: String?)

class Student(val name: String) : Person(null)

fun checkName(p: Person) {
    println(p.name);
}
fun main() {
    val p1 = Person("Jose"); // 1 field
    val s = Student("Ana");  // 2 fields: null + "Ana"       
    println(s.name);
    checkName(p1);
    checkName(s);
}
