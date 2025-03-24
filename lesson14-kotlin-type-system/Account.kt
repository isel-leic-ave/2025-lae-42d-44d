class Account {
    private val created: Long
    
    init {
        numberOfAccounts++ // <=> Account.nrOfAccounts++;
        created = System.currentTimeMillis()
    }
    // => class Account$Companion
    companion object {
        // => getNumberOfAccounts() in class Account$Companion
        var numberOfAccounts = 0
            private set
    }
}

fun main(args: Array<String>) {
    // <=> Java: Account.Companion.getNumberOfAccounts()
    println(Account.nrOfAccounts)
}