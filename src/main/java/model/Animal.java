package model;

import util.WorldSettings;

import java.util.List;

public class Animal implements WorldElement {
    private MapDirection direction;
    private Vector2d position;
    private int energy;

    private int ID;
    private int children_count;
    private Animal parent1;
    private Animal parent2;
    private List<Animal> children;

    private int grassEaten = 0;
    private int daysAlive = 0;
    private WorldSettings settings;

    int genom[];

//    public Animal() {
//        this.direction = MapDirection.NORTH;
//        this.position = new Vector2d(2, 2);
//    }

    public Animal(Vector2d position, MapDirection direction, Animal par1, Animal par2, int ID, int[] genom, WorldSettings settings) {
        this.position = position;
        this.direction = direction;
        this.parent1 = par1;
        this.parent2 = par2;
        this.ID = ID;
        this.genom = genom;
        this.settings = settings;
        this.energy = settings.animalEnergy();
    }
    public Animal(Vector2d position, MapDirection direction, Animal par1, Animal par2, int ID, int[] genom) {
        this.position = position;
        this.direction = direction;
        this.parent1 = par1;
        this.parent2 = par2;
        this.ID = ID;
        this.genom = genom;
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

    public void addChild(Animal dziecko){
        this.children.add(dziecko);
        actualizeAncestors(this);
    }

    public int[] getGenom() {
        return genom;
    }

    public void actualizeAncestors(Animal zwierz){
        int prev = zwierz.getChildren_count();
        zwierz.changeChildcount(prev + 1);
        Animal par1 = zwierz.getParent1();
        Animal par2 = zwierz.getParent2();

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

    public double getHealth() {
        return energy>settings.animalSatiety() ? 1.0 : (double) energy/settings.animalSatiety();
    }
}
