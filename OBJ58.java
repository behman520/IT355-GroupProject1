/**
 * OBJ58-J: Limit the extensibility of classes and methods with invariants.
 *
 * <p>{@link Amount} is immutable and declared {@code final}, so no subclass can turn
 * it into a mutable object and break the invariant that its value never changes.
 */
public class OBJ58 {

    /** An immutable amount: {@code final} class, private {@code final} field, no setters. */
    static final class Amount {

        /** The value; it never changes. */
        private final long value;

        /**
         * Creates an amount.
         *
         * @param value the value
         */
        Amount(long value) {
            this.value = value;
        }

        /**
         * Returns the value.
         *
         * @return the value
         */
        public long getValue() {
            return value;
        }

        /**
         * Adds another amount.
         *
         * @param other the amount to add
         * @return a new {@code Amount}; this object is not changed
         */
        public Amount add(Amount other) {
            return new Amount(value + other.value);
        }
    }

    /**
     * Shows that adding creates a new Amount and leaves the original unchanged.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Amount first = new Amount(123);
        Amount sum = first.add(new Amount(123));
        System.out.println("first = " + first.getValue()); // 123
        System.out.println("sum   = " + sum.getValue());// 246
    }
}