package simulation;

import javafx.application.Platform;
import javafx.scene.control.Button;
import model.*;
import util.RandomGenerator;
import util.WorldSettings;
import util.WorldStats;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation implements Runnable {
    private final WorldMap map;
    private final WorldSettings settings;
    private final WorldStats stats;
    private final RandomGenerator generator = new RandomGenerator();
    private final SimulationController controller;
    private boolean running = true;
    private boolean paused = false;
    private Thread simulationThread;

    int[] genGenome(int length) {
        int[] genome = new int[length];
        for (int i=0; i<length; i++)
            genome[i] = generator.randInt(0, 7);
        return genome;
    }

    public Simulation(WorldMap map, WorldSettings settings, SimulationController controller, WorldStats stats) {
        this.map = map;
        this.settings = settings;
        this.controller = controller;
        this.stats = stats;

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
        simulationThread = new Thread(() -> {
            while (running) {
                if (!paused && !map.getAlive().isEmpty()) {
                    controller.setDay(stats.getDay());
                    map.removeQueued();
                    map.addQueued();
                    map.sunrise();
                    stats.save();
                    stats.nextDay();
                }
                else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {}
                }
            }
        });
        simulationThread.start();
    }

    public void pause(Button pauseButton) {
        paused = !paused;
        Platform.runLater(() -> pauseButton.setText(paused? "Resume": "Pause"));
    }

    public void stop() {
        running = false;
        simulationThread.interrupt();
    }
}