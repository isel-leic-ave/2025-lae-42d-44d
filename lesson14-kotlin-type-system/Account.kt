class Account {
    val created: Long = System.currentTimeMillis()
    val balance: Long = 0
    companion object {
        var accountsCount: Int = 0  
    }
    init {
        accountsCount++
    }
}

fun main() {
    Account()
    Account()
    Account()
    // Kotlin Account.accountsCount ??           => JVM Account.accountsCount
    // accountsCount => static field de Account  => JVM Account.accountsCount ?NAO? pq é private
    // JVM: Account.Companion.getAccountsCount()
    println("Nr of accounts = ${Account.accountsCount}")
}
