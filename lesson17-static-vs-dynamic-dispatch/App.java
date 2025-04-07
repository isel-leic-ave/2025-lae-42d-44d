class Person {
    final String name;

    public Person() { this.name = null; }

    public Person(String name) {
        this.name = name;
    }
    // Virtual Method
    public void print() { System.out.println("Person");}
}
class Student extends Person {
    final String name;

    public Student(String name) {
        this.name = name;
    }
    // Override of the inherited method print()
    public void print() { System.out.println("Student");}
}
public class App {
    public static void checkName(Person p) {
        System.out.println(p.name); // Static Dispatch
        p.print(); // Dynamic Dispatch => The static Compiler cannot infer the method to be called
    }
    public static void main(String[] args) {
        Person p = new Person("Jose"); // 1 field
        Student s = new Student("Ana"); // 2 fields: null + "Ana"       
        System.out.println(p.name);
        System.out.println(s.name);
        checkName(p); // Jose 
        checkName(s); // null

    }
}
