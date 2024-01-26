package simulation;

import javafx.application.Platform;
import javafx.scene.control.Button;
import model.*;
import util.RandomGenerator;
import util.WorldSettings;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation implements Runnable {
    private final WorldMap map;
    private final WorldSettings settings;
    private final RandomGenerator generator = new RandomGenerator();
    public int day = 1;
    private final SimulationController controller;
    private boolean running = true;

    int[] genGenome(int length) {
        int[] genome = new int[length];
        for (int i=0; i<length; i++)
            genome[i] = generator.randInt(0, 7);
        return genome;
    }

    public Simulation(WorldMap map, WorldSettings settings, SimulationController controller) {
        this.map = map;
        this.settings = settings;
        this.controller = controller;

        for (int i=0; i<settings.animalCount(); i++) {
            Animal animal = new Animal(generator.genVector(map.getCurrentBonds().start(), map.getCurrentBonds().koniec()),
                    Arrays.asList(MapDirection.values()).get(ThreadLocalRandom.current().nextInt(0, 8)),
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
        while (running) {
            controller.setDay(day++);
            map.removeQueued();
            map.addQueued();
            map.sunrise();
        }
    }

    public void pause(Button pauseButton) {
        running = !running;
        Platform.runLater(() -> pauseButton.setText(running? "Pause": "Resume"));
        run();
    }
}