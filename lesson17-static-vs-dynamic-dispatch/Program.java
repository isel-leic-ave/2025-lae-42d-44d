public class Program {
    public static void main(String[] args) {
        Student s = new Student("Bart");
        Person p = s;
        System.out.println(s.name); // > Bart
        System.out.println(p.name); // > null

        // final Round rd = new Round(73.5362);
        // final Truncate tr = rd;
        // System.out.println(tr.cut(2)); // 73.54
        // System.out.println(rd.cut(2)); // 73.54
    }
}
