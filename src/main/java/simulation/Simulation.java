package simulation;

import model.*;
import util.RandomGenerator;
import util.WorldSettings;

public class Simulation implements Runnable {
//    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private final WorldSettings settings;
    private final RandomGenerator generator = new RandomGenerator();

    int[] genGenome(int length) {
        int[] genome = new int[length];
        for (int i=0; i<length; i++)
            genome[i] = generator.randInt(0, 7);
        return genome;
    }

    public Simulation(WorldMap map, WorldSettings settings) {
        this.map = map;
        this.settings = settings;

        for (int i=0; i<settings.animalCount(); i++) {
            Animal animal = new Animal(generator.genVector(map.getCurrentBonds().lowerLeft(), map.getCurrentBonds().upperRight()),
                    MapDirection.NORTH,
                    null,
                    null,
                    map.counter(),
                    genGenome(settings.genomeLength()),
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