package model;

import java.util.Objects;

public class Vector2d {
    private final int x, y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public boolean precedes(Vector2d other) {
        return x<=other.getX() && y<=other.getY();
    }
    public boolean follows(Vector2d other) {
        return x>=other.getX() && y>=other.getY();
    }
    public Vector2d add(Vector2d other) {
        return new Vector2d(x+other.getX(), y+other.getY());
    }
    public Vector2d subtract(Vector2d other) {
        return new Vector2d(x-other.getX(), y-other.getY());
    }
    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(Math.max(x, other.getX()), Math.max(y, other.getY()));
    }
    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(Math.min(x, other.getX()), Math.min(y, other.getY()));
    }
    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }
    @Override
    public boolean equals(Object other) {
        if (this==other)
            return true;
        if (!(other instanceof Vector2d that))
            return false;
        return x==that.getX() && y==that.getY();
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
