import static java.lang.System.out;

public class App {
    public static void main(String[] args) throws InterruptedException {
        out.println(ElapsedTimeCalculator.INSTANCE.elapsedTime());
        Thread.sleep(2500);
        out.println(ElapsedTimeCalculator.INSTANCE.elapsedTime());
        // new ElapsedTimeCalculator(); // NOT possible => constructor is private
    }
}
