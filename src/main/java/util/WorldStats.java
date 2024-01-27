package util;

import model.Animal;
import model.WorldMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class WorldStats {
    private final WorldMap map;
    private final WorldSettings settings;
    private int day = 1;
    private boolean saveStats = false;
    FileWriter fileWriter = null;

    public WorldStats(WorldMap map) {
        this.map = map;
        this.settings = map.getSettings();
    }
    public WorldStats(WorldMap map, boolean saveStats) {
        this.map = map;
        this.settings = map.getSettings();
        this.saveStats = saveStats;
        if (saveStats) initCSV();
    }

    public int getDay() {
        return day;
    }

    public void nextDay() {
        day++;
    }

    public int animalCount() {
        return map.getAlive().size();
    }

    public int grassCount() {
        return map.getGrass().size();
    }

    public int emptyTiles() {
        return settings.mapWidth()*settings.mapHeight()-map.getAnimals().size()-map.getGrass().size();
    }

    public double avgEnergy() {
        double sum = 0;
        for (Animal animal: map.getAlive())
            sum += animal.getEnergy();
        return animalCount()!=0 ? sum/animalCount() : 0;
    }

    public double avgLifespan() {
        double sum = 0;
        for (Animal animal: map.getDead())
            sum += animal.getLifespan();
        return animalCount()!=0 ? sum/animalCount() : 0;
    }

    public double avgChildCount() {
        double sum = 0;
        for (Animal animal: map.getAlive())
            sum += animal.getChildren_count();
        return animalCount()!=0 ? sum/animalCount() : 0;
    }

    public void initCSV() {
        String filename = new SimpleDateFormat(" EEE d MMM HH;mm;ss").format(new java.util.Date()) + " Simulation #" + map.getID() + ".csv";
        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                fileWriter = new FileWriter(filename, true);
                fileWriter.write("day,animalCount,grassCount,emptyTiles,avgEnergy,avgLifespan,avgChildCount\n");
            }
            else throw new IOException();
        } catch (IOException e) {
            saveStats = false;
        }
    }

    public void save() {
        if (!saveStats) return;
        try {
            fileWriter.write(String.format("%d,%d,%d,%d,%.1f,%.1f,%.1f\n", day, animalCount(), grassCount(), emptyTiles(), avgEnergy(), avgLifespan(), avgChildCount()));
        } catch (IOException e) {
            saveStats = false;
        }
    }

    public void closeFile() {
        try {
            fileWriter.close();
        } catch (IOException | NullPointerException ignore) {}
    }
}
