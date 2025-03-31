class Person {
    final String name;

    public Person(String name) {
        this.name = name;
    }
}
class Student extends Person {
    final String name;

    public Student(String name) {
        super(null);
        this.name = name;
    }
}
public class App {
    public static void checkName(Person p) {
        System.out.println(p.name);
    }
    public static void main(String[] args) {
        Person p1 = new Person("Jose"); // 1 field
        Student s = new Student("Ana"); // 2 fields: null + "Ana"       
        System.out.println(s.name);
        checkName(p1);
        checkName(s);
    }
}
