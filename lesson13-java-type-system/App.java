public class App {
    public static void main(String[] args) {
        // new I(); //  error: I is abstract; cannot be instantiated
        new I() {
            public void x() {
                System.out.println("Running method x");
            }
        };
    }
}