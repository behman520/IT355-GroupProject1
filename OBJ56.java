import java.util.Arrays;

/**
 * OBJ56-J: Provide sensitive mutable classes with unmodifiable wrappers.
 *
 * <p>{@link MutableProtector} is a read-only wrapper: its getter returns a copy and
 * its mutator throws {@link UnsupportedOperationException}, so it can be given to
 * untrusted code that only needs to read.
 */
public class OBJ56 {

    /** A sensitive class that holds a mutable array. */
    static class Mutable {

        /** Private internal state. */
        private int[] array;

        /**
         * Creates an object holding a copy of the given values.
         *
         * @param initial the starting values
         */
        Mutable(int[] initial) {
            this.array = initial.clone();
        }

        /**
         * Returns the values.
         *
         * @return a copy of the internal array
         */
        public int[] getArray() {
            return array.clone();
        }

        /**
         * Replaces the values.
         *
         * @param values the new values
         */
        public void setArray(int[] values) {
            this.array = values.clone();
        }
    }

    /** An unmodifiable view of {@link Mutable}: reading is allowed, changing is not. */
    static class MutableProtector extends Mutable {

        /**
         * Creates a read-only object holding a copy of the given values.
         *
         * @param initial the starting values
         */
        MutableProtector(int[] initial) {
            super(initial);
        }

        /**
         * Always fails, because this view cannot be modified.
         *
         * @param values ignored
         * @throws UnsupportedOperationException always
         */
        @Override
        public void setArray(int[] values) {
            throw new UnsupportedOperationException("Read-only view");
        }
    }

    /**
     * Acts as an untrusted caller that tries to change the data.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Mutable view = new MutableProtector(new int[] { 1, 2, 3 });
        System.out.println("Values: " + Arrays.toString(view.getArray())); // [1, 2, 3]

        try {
            view.setArray(new int[] { 7, 8, 9 });
        } catch (UnsupportedOperationException e) {
            System.out.println("setArray rejected: " + e.getMessage());
        }
    }
}