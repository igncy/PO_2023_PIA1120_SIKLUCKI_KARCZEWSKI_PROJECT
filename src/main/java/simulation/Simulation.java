package simulation;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
//    private final WorldMap map;

//    public simulation.Simulation(List<Vector2d> positions, WorldMap map) {
    public Simulation(List<Vector2d> positions) {
        for (Vector2d position: positions) {
//            try {
                Animal animal = new Animal(position);
//                map.place(animal);
                animals.add(animal);
//            } catch (PositionAlreadyOccupiedException exception) {
//                System.out.println(exception.getMessage());
//            }
        }
//        this.map = map;
    }

    @Override
    public void run() {
        int n = animals.size();
//        for (int i=0; i<directions.size(); i++) {
//            Animal animal = animals.get(i%n);
//            map.move(animal, directions.get(i));
//            try {
//                Thread.sleep(333);
//            } catch (InterruptedException ignore) {}
//        }
    }
}