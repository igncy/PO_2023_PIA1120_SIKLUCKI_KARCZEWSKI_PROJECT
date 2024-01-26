package model;

import util.MapChangeListener;
import util.WorldSettings;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.security.SecureRandom;

public abstract class AbstractWorldMap implements WorldMap {
    private Boundary bonds;
    protected HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    protected HashMap<Vector2d, Grass> grass = new HashMap<>();

    protected List<Animal> dead_animals;

    protected List<Grass> Graveyard_list;
    protected TreeSet<Vector2d> Graveyard_set;

    protected int ID;
    protected final WorldSettings settings;
    protected final ArrayList<MapChangeListener> observers = new ArrayList<>();

    public int counter = 0;

    public AbstractWorldMap(int ID, WorldSettings settings){
        this.settings = settings;
        this.ID = ID;
        bonds = new Boundary(
                new Vector2d(0, 0),
                new Vector2d(settings.mapWidth(), settings.mapHeight())
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

//    public void actualize_bonds(int x1, int y1){
//        Boundary act_border = getCurrentBounds();
//        int xp = act_border.lowerLeft().getX(); int yp = act_border.lowerLeft().getY();
//        int xk = act_border.upperRight().getX(); int yk = act_border.upperRight().getY();
//        if(x1 < xp) {
//            xp = x1;
//        }
//        if(x1 > xk){
//            xk = x1;
//        }
//        if(y1 < yp){
//            yp = y1;
//        }
//        if(y1 > yk){
//            yk = y1;
//        }
//        this.setBonds(new Boundary(new Vector2d(xp, yp), new Vector2d(xk, yk)));
//    }

    public void place(Animal stwor) {

        Vector2d val = stwor.getPosition();
//        actualize_bonds(val.getX(), val.getY());

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
            mapChanged("");
        }

    }

    public int[] generate_genom(Animal par1, Animal par2){

        int genomLen = 104; //przykładowe dane, będziemy brać z ustawień symualacji

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

    public void reproduce(Vector2d pos, List<Animal> group){

        Animal test = group.get(0);
        test.changeEnergy(-5);
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

        MapDirection direction = MapDirection.NORTH;
        Animal par1 = test; Animal par2 = test1;

        int[] child_genom = generate_genom(par1, par2);

        Animal child = new Animal(pos, direction, par1, par2, counter, child_genom);
        child.changeEnergy(2 * settings.animalBreedingEnergy());
        this.counter += 1;

        par1.addChild(child);
        par2.addChild(child);
        par1.changeEnergy(par1.getEnergy() - settings.animalBreedingEnergy());
        par2.changeEnergy(par2.getEnergy() - settings.animalBreedingEnergy());
        // tutaj być moze trzeba sprawdzić czy po rozmnażaniu któryś z rodziców nie umrze
    }


    public void sunrise(){

        int genomLen = settings.genomeLength(); // to tylko przykładowo podane, tak naprawdę jest to podawane jako parametr symulacji
        int energyBoost = settings.grassEnergy(); // Również przykładowa dana

        for (Map.Entry<Vector2d, List<Animal>> entry: this.animals.entrySet()) {
            List <Animal> lista = entry.getValue();
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


                Vector2d newpos = act.getPosition();
                act.changeEnergy(act.getEnergy() - 1);

                if(this.grass.containsKey(newpos)){
                    act.changeEnergy(act.getEnergy() + energyBoost);
                    Grass plant = this.grass.get(newpos);
                    plant.setActive(false);
                    this.grass.remove(plant);
                }
                else {
                    if(act.getEnergy() == 0){
                        act.setDead();

                        this.dead_animals.add(act);
                        Vector2d pos = act.getPosition();

                        if(!this.Graveyard_set.contains(pos))
                        {
                            this.Graveyard_set.add(pos);
                            this.Graveyard_list.add(new Grass(pos, true));
                        }
                        if(lista.size() == 1){
                            animals.remove(key);
                        }
                        else{
                            lista.remove(i);
                        }
                    }
                }
                if(act.getAlive()){
                    act.increaseDays();
                }

                for (Map.Entry<Vector2d, List<Animal>> el: this.animals.entrySet()){
                    List<Animal> group = el.getValue();
                    Vector2d act_pos = el.getKey();
                    if(group.size() > 1){
                        reproduce(act_pos, group);
                    }
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
    public int canMoveTo(Vector2d position) {
        int x = position.getX(); int y = position.getY();
        int x_min = this.bonds.start().getX(); int x_max = this.bonds.koniec().getX();
        boolean valid = (y >= this.bonds.start().getY() && y <= this.bonds.koniec().getY());
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
    public void mapChanged(String message) {
        for (MapChangeListener observer: observers)
            observer.mapChanged(this, message);
    }

    public int counter() {
        return counter++;
    }
}
