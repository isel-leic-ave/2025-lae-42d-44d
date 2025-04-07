class A {
    int foo(int x, int y) {
        int z =  x + y;
        return z;
    }
}

public class App {
    public static void main(String[] args) {
    }
    public static int inc(A a, int r) {
        // Quantos argumentos recebe o foo?
        // 3: this, x, y
        // bytecode: 
        // 3 push => load:
        // aload_0
        // iload_1
        // iconst_1 
        // invokevirtual #... // A.foo:(II)I
        return a.foo(r, 27);
    }
}