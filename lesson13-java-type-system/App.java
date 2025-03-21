public class App {
    public static void main(String[] args) {
        // new Z(); // error: Z is abstract; cannot be instantiated
        new Z() {
            public void x() {
                System.out.println("x");
            }
        };
    }
}