package simulation;

import model.*;
import util.RandomVectorGenerator;
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
        RandomVectorGenerator generator = new RandomVectorGenerator();
        int genome[] = {1,2,3,4,5};

        for (int i=0; i<settings.animalCount(); i++) {
            Animal animal = new Animal(generator.genVector(map.getCurrentBounds().lowerLeft(), map.getCurrentBounds().upperRight()),
                    MapDirection.NORTH,
                    null,
                    null,
                    1,
                    genome,
                    settings);
            map.place(animal);
        }
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