public class Boxing {
    public static void main(String[] args) {
        int n = 7; 
        Integer o = n; // <=> o = Integer.valueOf(n)
        int x = o;     // <=> x = n.intValue();
    }
    public void print(Object o) {
        System.out.println(o.getClass());
        System.out.println("Object = " + o);
    }
}
