class AJ {
    private final String name;
    public AJ(String name) {
        this.name = name;
    }
    // static class BJ { // Nested class => without this to the outer class
    class BJ { // Inner class => with a this$0 to the outer class
        public void print() {
            // System.out.println("Outer class AJ name = " + A.this.name);
            System.out.println("Outer class AJ name = " + name);
        }
    }
}