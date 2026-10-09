import java.util.HashSet;
import java.util.Set;

/**
 * OBJ57-J: Do not rely on methods that can be overridden by untrusted code.
 *
 * <p>{@link Widget} is {@code final}, so nobody can override its {@code equals()} or
 * {@code hashCode()}, and a {@link HashSet} of widgets can be trusted.
 */
public class OBJ57 {

    /** A widget. The class is {@code final}, so its methods cannot be overridden. */
    static final class Widget {

        /** Number of components. */
        private final int noOfComponents;

        /**
         * Creates a widget.
         *
         * @param noOfComponents the number of components
         */
        Widget(int noOfComponents) {
            this.noOfComponents = noOfComponents;
        }

        /**
         * Compares by number of components.
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is a widget with the same component count
         */
        @Override
        public boolean equals(Object o) {
            return o instanceof Widget && noOfComponents == ((Widget) o).noOfComponents;
        }

        /**
         * Hashes the component count.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return Integer.hashCode(noOfComponents);
        }
    }

    /**
     * Adds two equal widgets to a set; only one is kept.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Set<Widget> layouts = new HashSet<>();
        layouts.add(new Widget(1));
        layouts.add(new Widget(1));
        System.out.println("Layout size: " + layouts.size());//1
    }
}