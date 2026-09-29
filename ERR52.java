public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class ERR52 {
    private java.util.Map<String, Double> balances = new java.util.HashMap<>();

    // Compliant: Error condition is explicit in the method signature
    public double getBalance(String userId) throws UserNotFoundException {
        if (!balances.containsKey(userId)) {
            throw new UserNotFoundException("User ID " + userId + " does not exist.");
        }
        return balances.get(userId);
    }
}
