import static java.lang.System.out;

interface I { void virtualFoo(); }
class A {
    public static void foo() { out.println("A"); }
    public void virtualFoo(){ out.println("B"); }
}
class B extends A implements I {
    public static void foo() { out.println("B"); }
    public final void virtualFoo(){ out.println("B"); }
}
class C extends B {
    public static void foo(){ out.println("C"); }
}

public class Virtuals {
    public static void main(String[] args) {
        final C c = new C();
        final A a = c;
        final B b = c;
        final I i = c;
        a.foo();
        a.virtualFoo();
        b.foo();
        b.virtualFoo();
        c.foo();
        c.virtualFoo();
        i.virtualFoo();       
    }
}