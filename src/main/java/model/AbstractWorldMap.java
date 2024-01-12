package model;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWorldMap implements WorldMap {
    private Boundary bonds = new Boundary(new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE), new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE));
    protected HashMap<Vector2d, Animal> animals = new HashMap<>();
    protected HashMap<Vector2d, Grass> grass = new HashMap<>();

    private int ID;

    public AbstractWorldMap(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    public HashMap<Vector2d, Grass> getGrass() {
        return grass;
    }

    public void actualize_bonds(int x1, int y1){
        Boundary act_border = getCurrentBonds();
        int xp = act_border.start().getX(); int yp = act_border.start().getY();
        int xk = act_border.koniec().getX(); int yk = act_border.koniec().getY();
        if(x1 < xp) {
            xp = x1;
        }
        if(x1 > xk){
            xk = x1;
        }
        if(y1 < yp){
            yp = y1;
        }
        if(y1 > yk){
            yk = y1;
        }
        this.setBonds(new Boundary(new Vector2d(xp, yp), new Vector2d(xk, yk)));
    }


    public boolean place(Animal stwor) {
        Vector2d val = stwor.getPosition();
        boolean valid = false;
        if(canMoveTo(val)){
            actualize_bonds(val.getX(), val.getY());
            animals.put(val, stwor);
            valid = true;
        }
        return valid;
    }

    public void move(Animal stwor, MoveDirection direction){
        Vector2d prev = stwor.getPosition();
        Animal animal_test = new Animal(prev, stwor.getOrient());
        animal_test.move(direction, this);
        if (!canMoveTo(animal_test.getPosition())) {
            return;
        }

        animals.remove(prev);
        stwor.move(direction, this);
        place(stwor);
    }

    public WorldElement objectAt(Vector2d position){
        if(isOccupied(position)){
            return animals.get(position);
        }
        return null;
    }
    public boolean isOccupied(Vector2d position){
        return (animals.containsKey(position));
    }

    public HashMap<Vector2d, Animal> getAnimals() {
        return animals;
    }

    public boolean canMoveTo(Vector2d position) {
        return (!isOccupied(position));
    }

    public Boundary getCurrentBonds() {
        return bonds;
    }

    public void setBonds(Boundary bonds) {
        this.bonds = bonds;
    }

    public String toString(){
        Boundary limit = getCurrentBonds();
        return "XD";
    }



}
