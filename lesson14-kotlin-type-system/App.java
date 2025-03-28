public class App {
    public static void main(String[] args) throws Exception {
        // error: ElapsedTimeCalculator() has private access in ElapsedTimeCalculator
        // ElapsedTimeCalculator is a SINGLETON
        // new ElapsedTimeCalculator();
        System.out.println(ElapsedTimeCalculator.INSTANCE.elapsedTime());
        Thread.sleep(1500);
        System.out.println(ElapsedTimeCalculator.INSTANCE.elapsedTime());
        new Account();
        new Account();
        new Account();
        // <=> Kotlin: Account.accountsCount
        System.out.println("Nr of accounts = " + Account.Companion.getAccountsCount());
    }
}