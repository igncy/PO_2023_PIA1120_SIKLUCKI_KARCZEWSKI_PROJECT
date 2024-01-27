package util;

import model.Animal;
import model.WorldMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorldStats {
    private final WorldMap map;
    private final WorldSettings settings;
    private int day = 1;
    private boolean saveStats = false;
    FileWriter fileWriter = null;
    private final HashMap<String, Integer> genomes = new HashMap<>();

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
                fileWriter.write("day,animalCount,grassCount,emptyTiles,avgEnergy,avgLifespan,avgChildCount,mostCommonGenome,mostCommonGenomeCount\n");
            }
            else throw new IOException();
        } catch (IOException e) {
            saveStats = false;
        }
    }

    public void save() {
        if (!saveStats) return;
        try {
            String mcg = mostCommonGenome();
            fileWriter.write(String.format("%d,%d,%d,%d,%.1f,%.1f,%.1f,%s,%d\n", day, animalCount(), grassCount(), emptyTiles(), avgEnergy(), avgLifespan(), avgChildCount(), mcg, genomeCount(mcg)));
        } catch (IOException e) {
            saveStats = false;
        }
    }

    public void closeFile() {
        try {
            fileWriter.close();
        } catch (IOException | NullPointerException ignore) {}
    }

    private String genomeToString(int[] genome) {
        StringBuilder sb = new StringBuilder();
        for (int x: genome)
            sb.append(x);
        return sb.toString();
    }

    public String mostCommonGenome() {
        String mostCommon = "null";
        int mostCommonCount = -1;

        ArrayList<Animal> animals = new ArrayList<>();
        animals.addAll(map.getAlive());
        animals.addAll(map.getDead());

        for (Animal animal: animals) {
            String genome = genomeToString(animal.getGenom());
            System.out.println(genome);
            int count = genomes.getOrDefault(genome, 0)+1;
            genomes.put(genome, count);

            if (count > mostCommonCount) {
                mostCommon = genome;
                mostCommonCount = count;
            }
        }

        return mostCommon;
    }

    public int genomeCount(String genome) {
        return genomes.getOrDefault(genome, 0);
    }
}
