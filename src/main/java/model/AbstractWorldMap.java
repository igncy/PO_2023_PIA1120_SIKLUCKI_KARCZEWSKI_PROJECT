package model;

import util.WorldSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements WorldMap {
    private Boundary bonds;
    protected HashMap<Vector2d, List<Animal> > animals = new HashMap<>();
    protected HashMap<Vector2d, Grass> grass = new HashMap<>();

    private int ID;

    public AbstractWorldMap(int ID, WorldSettings settings){
        this.ID = ID;
        bonds = new Boundary(
                new Vector2d(0, settings.mapWidth()),
                new Vector2d(0, settings.mapHeight())
        );
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


    public void place(Animal stwor) {
        Vector2d val = stwor.getPosition();
        actualize_bonds(val.getX(), val.getY());

        /* I wersja
        if(animals.containsKey(val)){
            List<Animal> lista = animals.get(val);
            animals.remove(val);
            lista.add(stwor);
            animals.put(val, lista);
        }
        else{
            List<Animal> nowa_lista = new ArrayList<>();
            nowa_lista.add(stwor);
            animals.put(val, nowa_lista);
        }
        */

        if(animals.containsKey(val)){
            List<Animal> lista = animals.get(val);
            lista.add(stwor);
        }
        else{
            animals.put(val, List.of(stwor));
        }


    }

    public int[] generate_genom(Animal par1, Animal par2){

        int genomLen = 104;

        int E1 = par1.getEnergy(); int E2 = par2.getEnergy();
        float percent = (float) E1/(E1 + E2);
        int x = (int) Math.floor(percent * genomLen);
        int sideChoice = ThreadLocalRandom.current().nextInt(1, 3);
        int p1 = 0; int k1 = 0; int p2 = 0; int k2 = 0;

        if(sideChoice == 1)
        {
            p1 = 0; k1 = x;
            p2 = x; k2 = genomLen-1;
        }
        else
        {
            p1 = genomLen - x; k1 = genomLen - 1;
            p2 = 0; k2 = genomLen - x;
        }

        int childGenom[] = new int[genomLen];
        for(int i = p1; i <= k1; i++){
            childGenom[i] = par1.getGenom()[i];
        }

        for (int i = p2; i <= k2; i++){
            childGenom[i] = par2.getGenom()[i];
        }

        return childGenom;
    }

    public void reproduce(Animal nowy, List<Animal> grupa){
        Vector2d pos = nowy.getPosition();
        int min_energy = 5;
        int lost_energy = 3;
        for (int i = 0; i < grupa.size(); i++){
            Animal temp = grupa.get(i);
            MapDirection dir = MapDirection.NORTH;
            int[] child_genom = {1, 2, 3};
            if(temp.getEnergy() >= min_energy) {
                Animal child = new Animal(pos, dir,  nowy, temp, 5, child_genom);
        }

        }
    }


    /*
    public void move(Animal stwor, MoveDirection direction){
        Vector2d prev = stwor.getPosition();
        Animal animal_test = new Animal(prev, stwor.getOrient(), null ,null, -1);
        animal_test.move(direction, this);
        if (!canMoveTo(animal_test.getPosition())) {
            return;
        }
        animals.remove(prev);
        stwor.move(direction, this);
        place(stwor);
    }
    */




    public List<Animal> objectAt(Vector2d position){
        if(isOccupied(position)){
            return animals.get(position);
        }
        return null;
    }
    public boolean isOccupied(Vector2d position){
        return (animals.containsKey(position));
    }

    public HashMap<Vector2d, List<Animal> > getAnimals() {
        return animals;
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

    @Override
    public boolean canMoveTo(Vector2d position) {
        return false;
    }
}
