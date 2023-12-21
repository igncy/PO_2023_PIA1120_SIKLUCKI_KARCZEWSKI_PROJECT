package model;

public class Animal implements WorldElement {
    private MapDirection direction;
    private Vector2d position;

    public Animal() {
        this.direction = MapDirection.NORTH;
        this.position = new Vector2d(2, 2);
    }
    public Animal(Vector2d position) {
        this.position = position;
        this.direction = MapDirection.NORTH;
    }

    @Override
    public String toString() {
        return switch (direction) {
            case NORTH -> "^";
            case EAST -> ">";
            case SOUTH -> "v";
            case WEST -> "<";
        };
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public Vector2d getPosition() {
        return position;
    }

    public void move(MoveDirection direction, MoveValidator validator) {
        Vector2d position = this.position;

        switch (direction) {
            case RIGHT -> {
                this.direction = this.direction.next();
                return;
            }
            case LEFT -> {
                this.direction = this.direction.previous();
                return;
            }
            case FORWARD -> position = position.add(this.direction.toUnitVector());
            case BACKWARD -> position = position.subtract(this.direction.toUnitVector());
        }
        if (validator.canMoveTo(position))
            this.position = position;
    }
}
