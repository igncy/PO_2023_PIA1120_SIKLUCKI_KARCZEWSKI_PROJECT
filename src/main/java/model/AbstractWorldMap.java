package model;

import util.MapChangeListener;
import util.WorldSettings;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements WorldMap {
    private Boundary bonds;
    protected final HashMap<Vector2d, ArrayList<Animal>> animals = new HashMap<>();
    protected final HashMap<Vector2d, Grass> grass = new HashMap<>();
    protected final ArrayList<Animal> dead = new ArrayList<>();
    protected final ArrayList<Animal> animalsAlive = new ArrayList<>();

    private final int ID;
    protected final WorldSettings settings;
    protected final ArrayList<MapChangeListener> observers = new ArrayList<>();
    protected final HashMap<Vector2d, Animal> toRemove = new HashMap<>();
    protected final HashMap<Vector2d, Animal> toAdd = new HashMap<>();

    public int counter = 0;

    public AbstractWorldMap(int ID, WorldSettings settings){
        this.settings = settings;
        this.ID = ID;
        bonds = new Boundary(
                new Vector2d(0, 0),
                new Vector2d(settings.mapWidth()-1, settings.mapHeight()-1)
        );
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

    public void reproduce(Animal act, List<Animal> group){
        if (group == null) return;

        Vector2d pos = act.getPosition();

        int min_energy = 5;
        int lost_energy = 3;

        for (int i = 0; i < group.size(); i++){
            Animal temp = group.get(i);
            MapDirection dir = Arrays.asList(MapDirection.values()).get(ThreadLocalRandom.current().nextInt(0, 8));

            int[] child_genom = generate_genom(act, temp);

            if(temp.getID() != act.getID() && temp.getEnergy() >= min_energy) {
                Animal child = new Animal(pos, dir, act, temp, counter, child_genom, settings);
                act.addChild(child);
                temp.addChild(child);
                temp.changeEnergy(temp.getEnergy() - lost_energy);
                act.changeEnergy(temp.getEnergy() - lost_energy);
                toAdd.put(pos, child);
                // tutaj być moze trzeba sprawdzić czy po rozmnażaniu któryś z rodziców nie umrze
            }
        }
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

                int prevX = act.getPosition().getX(); int prevY = act.getPosition().getY();
                act.move(MoveDirection.FORWARD, this);

                mapChanged();
                try {
                    Thread.sleep(settings.sleepTime());
                } catch (InterruptedException ignore) {}

                if(act.getPosition().getX() != prevX || act.getPosition().getY() != prevY){
                    reproduce(act, animals.get(act.getPosition()));
                }

                Vector2d newpos = act.getPosition();
                act.changeEnergy(act.getEnergy() - settings.energyLoss());
                if(act.getEnergy() <= 0){
                    act.setDead();
                    this.dead.add(act);
                    toRemove.put(key, act);
                    return;
                }

                if(this.grass.containsKey(newpos)){
                    act.changeEnergy(act.getEnergy() + energyBoost);
//                    Grass plant = this.grass.get(newpos);
                    this.grass.remove(newpos); //te 2 linijki można zawrzeć w 1 linijce ale wyciągnięcie plant mogłoby się przydać
                }
                else {
                    if(act.getEnergy() == 0){
                        act.setDead();
                        this.dead.add(act);
                        toRemove.put(key, act);
                    }
                }
                if(act.getAlive()){
                    act.increaseDays();
                }
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
            if (animals.get(pos)!=null && animals.get(pos).size() > 1) animals.get(pos).remove(obj);
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
