package model;

import java.util.List;

public class Animal implements WorldElement {
    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private int children_count;
    private Animal parent1;
    private Animal parent2;
    private List<Animal> children;

    public Animal() {
        this.direction = MapDirection.NORTH;
        this.position = new Vector2d(2, 2);
    }

    public Animal(Vector2d position, MapDirection direction, Animal par1, Animal par2) {
        this.position = position;
        this.direction = direction;
        this.parent1 = par1;
        this.parent2 = par2;
    }

    @Override
    public String toString() {
        return switch (direction) {
            case NORTH -> "^";
            case EAST -> ">";
            case SOUTH -> "v";
            case WEST -> "<";
            default -> "$";
        };
    }

    public void addChild(Animal gnoj){
        this.children.add(gnoj);
        actualizeAncestors(this);
    }


    public void actualizeAncestors(Animal dupa){
        int prev = dupa.getChildren_count();
        dupa.changeChildcount(prev + 1);
        Animal par1 = dupa.getParent1();
        Animal par2 = dupa.getParent2();

        if(par1 != null){
            actualizeAncestors(par1);
        }

        if(par2 != null){
            actualizeAncestors(par2);
        }
    }


    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public Vector2d getPosition() {
        return position;
    }

    public MapDirection getOrient() {
        return direction;
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

    public void changeEnergy(int En){
        this.energy = En;
    }

    public int getChildren_count() {
        return children_count;
    }

    public void changeChildcount(int x){
        this.children_count = x;
    }

    public Animal getParent1() {
        return parent1;
    }

    public Animal getParent2(){
        return parent2;
    }

    public int getEnergy() {
        return energy;
    }
}
