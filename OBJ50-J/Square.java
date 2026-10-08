public class Square {
    private int sideLength; //the side length of a square

    Square(int s )
    {
        this.sideLength = s;
    }

    public int getSideLength() {
        return sideLength;
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public int calculateArea()
    {
        return this.getSideLength() *2;
    }

}
