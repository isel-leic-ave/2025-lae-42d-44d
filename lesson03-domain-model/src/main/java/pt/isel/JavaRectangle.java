package pt.isel;

public class JavaRectangle {
    private final int height;
    private final int width;
    public JavaRectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getArea() {
        return width * height;
    }
}
