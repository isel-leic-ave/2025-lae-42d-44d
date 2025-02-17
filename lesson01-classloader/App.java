public class App {
    public static void main(String[] args) {
        System.out.println("Press any key to proceed...");
        System.console().readLine();
        new A().print();
    }
    public void bar() {
        new C().print();
    }
}
