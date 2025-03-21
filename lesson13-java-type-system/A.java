class A {
    private String name;
    public A(String name) {
        this.name = name;
    }
    // static class B {} // Nested class
    class B { // Inner class
        public void print() {
            // System.out.println("Outer name of A is " + name);
            System.out.println("Outer name of A is " + A.this.name);
        }
    }
}