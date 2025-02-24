package pt.isel;

public class JavaRectangle {
    private final int height;
    private final int width;
    public JavaRectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public final int getHeight() {
        return height;
    }

    public final int getWidth() {
        return width;
    }

    public final int getArea() {
        return width * height;
    }
}
