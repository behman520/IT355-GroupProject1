/**
 * Demonstrates that declaring an object reference as final does not make
 * the object itself immutable.
 * <p>
 * The square variable is  final, so it can never be reassigned to
 * refer to a differentSquare. However, the state of the object it refers
 * to can still change through its methods, such as  setSideLength(int).
 * The program prints the area before and after changing the side length to show
 * this.
 */
public class Main
{
    public static void main(String[] args) {
        final Square square = new Square(5);

        System.out.println("initial area: " + square.calculateArea());

        square.setSideLength(10);

        System.out.println("After reassign: " + square.calculateArea());

    }
}