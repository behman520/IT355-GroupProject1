public class Main
{
    public static void main(String[] args) {
        final Square square = new Square(5);

        System.out.println("initial area: " + square.calculateArea());

        square.setSideLength(10);

        System.out.println("After reassign: " + square.calculateArea());

    }
}