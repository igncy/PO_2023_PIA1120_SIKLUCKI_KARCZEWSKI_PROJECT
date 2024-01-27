package model;

import util.MapChangeListener;
import util.WorldSettings;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements WorldMap {
    private Boundary bonds;
    protected final HashMap<Vector2d, ArrayList<Animal>> animals = new HashMap<>();
    protected final HashMap<Vector2d, Grass> grass = new HashMap<>();
    protected final ArrayList<Animal> dead = new ArrayList<>();
    protected final ArrayList<Animal> animalsAlive = new ArrayList<>();
    protected int total_area;
    protected int GreenPlaces_occupied = 0;
    protected int occupiedCaracass = 0;
    protected final int ID;
    protected final WorldSettings settings;
    protected final ArrayList<MapChangeListener> observers = new ArrayList<>();
    protected final HashMap<Vector2d, Animal> toRemove = new HashMap<>();
    protected final HashMap<Vector2d, Animal> toAdd = new HashMap<>();
    protected List<Grass> Graveyard_list;
    protected TreeSet<Vector2d> Graveyard_set;

    public int counter = 0;

    public AbstractWorldMap(int ID, WorldSettings settings){
        this.settings = settings;
        this.ID = ID;
        bonds = new Boundary(
                new Vector2d(0, 0),
                new Vector2d(settings.mapWidth()-1, settings.mapHeight()-1)
        );
        this.total_area = settings.mapHeight() * settings.mapWidth();
    }

    public int getID(){
        return ID;
    }

    public HashMap<Vector2d, Grass> getGrass() {
        return grass;
    }

    public WorldSettings getSettings() {
        return settings;
    }

    public void place(Animal stwor) {

        Vector2d val = stwor.getPosition();

        if(animals.containsKey(val)){
            ArrayList<Animal> lista = animals.get(val);
            lista.add(stwor);
        }
        else{
            animals.put(val, new ArrayList<>(Arrays.asList(stwor)));
        }

        if (!animalsAlive.contains(stwor)) animalsAlive.add(stwor);
    }

    public int[] generate_genom(Animal par1, Animal par2){

        int genomLen = settings.genomeLength();

        int E1 = par1.getEnergy(); int E2 = par2.getEnergy();
        float percent = (float) E1/(E1 + E2);
        int x = (int) Math.floor(percent * genomLen);
        int sideChoice = ThreadLocalRandom.current().nextInt(1, 3);
        int p1 = 0; int k1 = 0; int p2 = 0; int k2 = 0;

        if(sideChoice == 1)
        {
            p1 = 0; k1 = x-1;
            p2 = x; k2 = genomLen-1;
        }
        else
        {
            p1 = genomLen - x; k1 = genomLen - 1;
            p2 = 0; k2 = genomLen - x - 1;
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

    public void reproduce(Vector2d pos, List<Animal> group){

        Animal test = group.get(0);
        for(int i = 1; i < group.size(); i++)
        {
            Animal z1 = group.get(i);
            if(z1.comp(test)){
                test = z1;
            }
        }

        if(test.getEnergy() < settings.animalEnergy()){
            return;
        }

        Animal test1;
        int ID_prev = test.getID();

        if(group.get(0).getID() == ID_prev){ test1 = group.get(1); }
        else { test1 = group.get(0); }

        for (int i = 0; i < group.size(); i++)
        {
            Animal z1 = group.get(i);
            if(z1.getID() != ID_prev && z1.comp(test1)){
                test1 = z1;
            }
        }

        if(test1.getEnergy() < settings.animalEnergy()){
            return;
        }

        List<MapDirection> directions= List.of(MapDirection.NORTH, MapDirection.NORTHEAST, MapDirection.EAST, MapDirection.SOUTHEAST, MapDirection.SOUTH, MapDirection.SOUTHWEST, MapDirection.WEST, MapDirection.NORTHWEST);
        SecureRandom rand1 = new SecureRandom();
        MapDirection direction = directions.get(rand1.nextInt(8));
        Animal par1 = test; Animal par2 = test1;

        int[] child_genom = generate_genom(par1, par2);

        Animal child = new Animal(pos, direction, par1, par2, counter(), child_genom);
        child.changeEnergy(2 * settings.animalBreedingEnergy());

        par1.addChild(child);
        par2.addChild(child);
        par1.changeEnergy(par1.getEnergy() - settings.animalBreedingEnergy());
        par2.changeEnergy(par2.getEnergy() - settings.animalBreedingEnergy());
    }


    public void sunrise(){

        int genomLen = settings.genomeLength();
        int energyBoost = settings.grassEnergy();

        for (Map.Entry<Vector2d, ArrayList<Animal>> entry: this.animals.entrySet()) {
            ArrayList<Animal> lista = entry.getValue();
            Vector2d key = entry.getKey();

            for (int i = 0; i < lista.size(); i++) {

                Animal act = lista.get(i);

                int lived = act.getDays_of_life();
                int move = act.getGenom()[lived % genomLen];

                for (int j = 0; j < move; j++){
                    act.move(MoveDirection.RIGHT, this);
                }

                act.move(MoveDirection.FORWARD, this);

                mapChanged();
                try {
                    Thread.sleep(settings.sleepTime());
                } catch (InterruptedException ignore) {}


                Vector2d newpos = act.getPosition();
                act.changeEnergy(act.getEnergy() - 1);

                if(this.grass.containsKey(newpos)){
                    act.changeEnergy(act.getEnergy() + energyBoost);
                    Grass plant = grass.get(newpos);
                    if(Graveyard_set.contains(newpos)){
                        plant.setActive(false);
                        this.occupiedCaracass -= 1;
                    }
                    else{
                        GreenPlaces_occupied -= 1;
                    }
                    this.grass.remove(newpos);
                }
                else {
                    if(act.getEnergy() == 0){
                        act.setDead();
                        Vector2d pos = act.getPosition();
                        this.dead.add(act);
                        if(!this.Graveyard_set.contains(pos))
                        {
                            this.Graveyard_set.add(pos);
                            this.Graveyard_list.add(new Grass(pos, false));
                        }
                        toRemove.put(key, act);
                    }
                }
                if(act.getAlive()){
                    act.increaseDays();
                }
            }

        }

        for (Map.Entry<Vector2d, ArrayList<Animal>> entry: this.animals.entrySet()) {
            ArrayList<Animal> group = entry.getValue();
            Vector2d key = entry.getKey();
            if(group.size() > 1)
            {
                reproduce(key, group);
            }
        }





    }


    public List<Animal> objectAt(Vector2d position){
        if(isOccupied(position)){
            return animals.get(position);
        }
        return null;
    }
    public boolean isOccupied(Vector2d position){
        return (animals.containsKey(position));
    }

    public HashMap<Vector2d, ArrayList<Animal>> getAnimals() {
        return animals;
    }

    public Boundary getCurrentBonds() {
        return bonds;
    }

    public void setBonds(Boundary bonds) {
        this.bonds = bonds;
    }

    @Override
    public int canMoveTo(Vector2d position) {
        int x = position.getX(); int y = position.getY();
        int x_min = this.bonds.start().getX(); int x_max = this.bonds.koniec().getX()-1;
        boolean valid = (y >= this.bonds.start().getY() && y <= this.bonds.koniec().getY()-1);

        if(!valid) { return 0; } //
        else{
            if(x < x_min){ return -1; } // lewy brzeg
            else if(x > x_max) { return 1; } // prawy brzeg
            else{ return 2; } // brak ograniczeń
        }
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }
    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }
    public void mapChanged() {
        for (MapChangeListener observer: observers)
            observer.mapChanged(this);
    }

    public int counter() {
        return counter++;
    }

    public ArrayList<Animal> getDead() {
        return dead;
    }

    public ArrayList<Animal> getAlive() {
        return animalsAlive;
    }

    public void removeQueued() {
        for (Map.Entry<Vector2d, Animal> toRem: toRemove.entrySet()) {
            Vector2d pos = toRem.getKey();
            Animal obj = toRem.getValue();
            animalsAlive.remove(obj);
            if (animals.get(pos).size() > 1) animals.get(pos).remove(obj);
            else animals.remove(pos);
        }
        toRemove.clear();
    }
    public void addQueued() {
        for (Map.Entry<Vector2d, Animal> toRem: toAdd.entrySet()) {
            place(toRem.getValue());
        }
        toAdd.clear();
    }
}
