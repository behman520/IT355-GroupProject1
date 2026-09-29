public class ERR04 {
    public static int divideByZero() {
        try {
            return 10 / 0; // Throws ArithmeticException and propagates to caller
        } finally {
            // Compliant: Only perform resource cleanup or logging here
            System.out.println("Cleanup tasks executed successfully.");
        }
    }
}
