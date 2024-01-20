package simulation;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final ArrayList<Simulation> simulations;
    private final ArrayList<Thread> threads = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public SimulationEngine(ArrayList<Simulation> simulations) {
        this.simulations = simulations;
    }

    public void runSync() {
        for (Simulation simulation: simulations)
            simulation.run();
    }

    public void runAsync() {
        for (Simulation simulation: simulations) {
            Thread simulationThread = new Thread(simulation);
            threads.add(simulationThread);
            simulationThread.start();
        }
    }

    public void runAsyncInThreadPool() {
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (Simulation simulation: simulations)
            executorService.submit(simulation);
        executorService.shutdown();
    }

    public void awaitSimulationsEnd() {
        if (!threads.isEmpty()) {
            for (Thread thread: threads) {
                try {
                    thread.join();
                } catch (InterruptedException ignore) {}
            }
        }
        else {
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {}
        }
    }
}