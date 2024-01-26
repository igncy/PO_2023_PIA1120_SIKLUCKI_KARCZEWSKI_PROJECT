package model;

public class Grass implements WorldElement {
    private final Vector2d position;

    private boolean active = true;

    public Grass(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean B){
        active = B;
    }
    @Override
    public String toString() {
        return "*";
    }
}
