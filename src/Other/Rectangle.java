package Other;

import processing.core.PVector;

public class Rectangle {
    private PVector position;
    private float width;
    private float height;

    public Rectangle(PVector position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Rectangle() {
        this.position = new PVector(0,0);
        this.width = 0;
        this.height = 0;
    }

    public float getSquareSize(){
        return width*height;
    }

    public PVector getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "\nposition=" + position +
                ", \nwidth=" + width +
                ", \nheight=" + height +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rectangle))
            return false;
        Rectangle r = (Rectangle)o;
        return (position.x == r.position.x)
                && (position.y == r.position.y)
                && (width == r.width)
                && (height == r.height);
    }
}
