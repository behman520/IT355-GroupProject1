/**
 * OBJ11-J: Be wary of letting constructors throw exceptions.
 *
 * <p>The class below is {@code final} so no subclass can capture a partially
 * initialized object, and it validates its input before assigning the {@code final}
 * field.
 */
public class OBJ11 {

    /** An account that is either fully initialized or never created. */
    static final class Account {

        /** The account owner. */
        private final String owner;

        /**
         * Creates an account after validating the owner.
         *
         * @param owner the owner's name; must not be null or blank
         * @throws IllegalArgumentException if the owner is null or blank
         */
        Account(String owner) {
            if (owner == null || owner.isBlank()) {
                throw new IllegalArgumentException("Owner is required");
            }
            this.owner = owner;
        }

        /**
         * Returns the owner.
         *
         * @return the owner's name
         */
        public String getOwner() {
            return owner;
        }
    }

    /**
     * Creates one valid account and attempts one invalid account.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Created: " + new Account("Alice").getOwner());

        Account rejected = null;
        try {
            rejected = new Account(" ");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        System.out.println("Reference after failed construction: " + rejected); // null
    }
}