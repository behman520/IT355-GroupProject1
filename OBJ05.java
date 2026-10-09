import java.util.Date;

/**
 * OBJ05-J: Do not return references to private mutable class members.
 *
 * <p>{@link Date} is mutable. If a getter returned the private field itself,
 * callers could change the object's internal state. The getter below returns a
 * defensive copy instead.
 */
public class OBJ05 {

    /** A person whose birth date cannot be changed from outside the class. */
    static final class Person {

        /** Private mutable member that must never be exposed directly. */
        private final Date birthDate;

        /**
         * Creates a person with the given birth date.
         *
         * @param birthDate the birth date; it is copied, so later changes to the argument do not affect this object
         */
        Person(Date birthDate) {
            this.birthDate = new Date(birthDate.getTime());
        }

        /**
         * Returns the birth date.
         *
         * @return a copy of the birth date, never the internal object
         */
        public Date getBirthDate() {
            return new Date(birthDate.getTime());
        }
    }

    /**
     * Shows that changing the returned Date does not change the Person.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Person person = new Person(new Date(0L));

        person.getBirthDate().setTime(999L); // only changes the copy

        System.out.println("Birth date (ms): " + person.getBirthDate().getTime()); // 0
    }
}