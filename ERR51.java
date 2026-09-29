// Compliant: Define a domain-specific exception class
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

public class ERR51 {
    private double balance = 100.0;

    // Compliant: The specific exception is explicitly part of the method contract
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Cannot withdraw $" + amount + " from balance $" + balance);
        }
        balance -= amount;
    }
}
