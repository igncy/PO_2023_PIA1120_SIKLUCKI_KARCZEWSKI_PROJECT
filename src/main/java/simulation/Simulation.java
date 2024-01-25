package simulation;

import model.*;
import util.WorldSettings;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
//    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private final WorldSettings settings;

    public Simulation(WorldMap map, WorldSettings settings) {
        this.map = map;
        this.settings = settings;
//        for (Vector2d position: positions) {
//            try {
//                Animal animal = new Animal(position);
//                map.place(animal);
//                animals.add(animal);
//            } catch (PositionAlreadyOccupiedException exception) {
//                System.out.println(exception.getMessage());
//            }
//        }
    }

    @Override
    public void run() {
//        int n = animals.size();
//        for (int i=0; i<directions.size(); i++) {
//            Animal animal = animals.get(i%n);
//            map.move(animal, directions.get(i));
//            try {
//                Thread.sleep(333);
//            } catch (InterruptedException ignore) {}
//        }
    }
}