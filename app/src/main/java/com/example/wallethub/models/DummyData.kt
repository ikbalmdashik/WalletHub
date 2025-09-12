// This class simulates a simple database by holding all the dummy data and related functions.
class Database {
    // A data class to model a single transaction
    data class Transaction(
        val id: String,
        val description: String,
        val amount: Double,
        val type: TransactionType
    )

    // An enum to represent the type of transaction
    enum class TransactionType {
        SENT, RECEIVED, PAYMENT, WITHDRAWAL
    }

    // A data class to model a user with their associated data
    data class User(
        val email: String,
        val name: String,
        val balance: Double,
        val transactions: List<Transaction>,
        val passwordHash: String,
        val phone: String,
        val address: String
    )

    // A list of dummy users, based in Dhaka, Bangladesh, with dollar currency
    private val dummyUsers = listOf(
        User(
            email = "ashik@example.com",
            name = "Ashik",
            balance = 1250.75,
            transactions = listOf(
                Transaction("T101", "Grocery Store", -35.25, TransactionType.PAYMENT),
                Transaction("T102", "Freelance Gig", 500.00, TransactionType.RECEIVED),
                Transaction("T103", "Online Subscription", -12.99, TransactionType.PAYMENT),
                Transaction("T104", "ATM Withdrawal", -100.00, TransactionType.WITHDRAWAL)
            ),
            passwordHash = "123456",
            phone = "+8801712345678",
            address = "123 Kazi Nazrul Islam Ave, Dhaka"
        ),
        User(
            email = "ikbal@example.com",
            name = "Ikbal",
            balance = 540.25,
            transactions = listOf(
                Transaction("T201", "Dinner with Friends", -45.50, TransactionType.PAYMENT),
                Transaction("T202", "Transfer from Mother", 150.00, TransactionType.RECEIVED),
                Transaction("T203", "Online Purchase", -88.00, TransactionType.PAYMENT),
                Transaction("T204", "Money Sent to Friend", -25.00, TransactionType.SENT)
            ),
            passwordHash = "123456",
            phone = "+8801812345679",
            address = "456 Mirpur Road, Dhaka"
        ),
        User(
            email = "pial@example.com",
            name = "Pial",
            balance = 8900.00,
            transactions = listOf(
                Transaction("T301", "Electricity Bill", -120.00, TransactionType.PAYMENT),
                Transaction("T302", "Investment Payout", 500.00, TransactionType.RECEIVED),
                Transaction("T303", "Shopping at Mall", -75.50, TransactionType.PAYMENT),
                Transaction("T304", "Lunch", -15.75, TransactionType.PAYMENT),
                Transaction("T305", "ATM Withdrawal", -300.00, TransactionType.WITHDRAWAL)
            ),
            passwordHash = "123456",
            phone = "+8801912345680",
            address = "789 Dhanmondi, Dhaka"
        ),
        User(
            email = "tanvir@example.com",
            name = "Tanvir",
            balance = 720.50,
            transactions = listOf(
                Transaction("T401", "Gasoline", -40.00, TransactionType.PAYMENT),
                Transaction("T402", "Movie Tickets", -25.00, TransactionType.PAYMENT),
                Transaction("T403", "Birthday Gift", -60.00, TransactionType.SENT),
                Transaction("T404", "Received from Relative", 75.00, TransactionType.RECEIVED)
            ),
            passwordHash = "123456",
            phone = "+8801612345681",
            address = "101 Gulshan Avenue, Dhaka"
        ),
        User(
            email = "abdullah@example.com",
            name = "Abdullah",
            balance = 300.00,
            transactions = listOf(
                Transaction("T501", "Online Course", -150.00, TransactionType.PAYMENT),
                Transaction("T502", "Loan Repayment", 250.00, TransactionType.RECEIVED),
                Transaction("T503", "Subscription Service", -10.00, TransactionType.PAYMENT)
            ),
            passwordHash = "123456",
            phone = "+8801512345682",
            address = "202 Uttara, Dhaka"
        ),
        User(
            email = "nomal@example.com",
            name = "Noman",
            balance = 300.00,
            transactions = listOf(
                Transaction("T501", "Online Course", -150.00, TransactionType.PAYMENT),
                Transaction("T502", "Loan Repayment", 250.00, TransactionType.RECEIVED),
                Transaction("T503", "Subscription Service", -10.00, TransactionType.PAYMENT)
            ),
            passwordHash = "123456",
            phone = "+8801712345683",
            address = "303 Old Dhaka, Dhaka"
        )
    )

    // This function checks if a given email and password combination exists.
    fun validateLogin(email: String?, password: String): Boolean {
        // Use the 'any' function to check if at least one user
        // matches BOTH the provided email and password.
        return dummyUsers.any { it.email == email && it.passwordHash == password }
    }

    // A function to get a user by their email
    fun getUser(email: String?): User? {
        return dummyUsers.find { it.email == email }
    }
}
