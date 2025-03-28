public class ElapsedTimeCalculator {
    // The singleton instance
    public static final ElapsedTimeCalculator INSTANCE;

    static {
        INSTANCE = new ElapsedTimeCalculator();
    }

    private static final long startTime = 
        System.currentTimeMillis();

    // Avoid the instantiation of this class
    // out of the scope of this class.
    private ElapsedTimeCalculator() {
    }
    public long elapsedTime() {
        long currentTime = System.currentTimeMillis();
        return currentTime - startTime;
    }
}
