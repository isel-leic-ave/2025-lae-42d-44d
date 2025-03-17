package pt.isel;

public class SavingsAccount {
    private static int accountsCount; // Don't belong to objects
    private short accountCode; // 2 bytes
    private String holderName; // 8 bytes
    private long balance;      // 8 bytes
    private boolean isActive;  // 1 byte
    private final double interestRate = 0.02; // 8 bytes
}