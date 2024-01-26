package util;

import model.Animal;
import model.WorldMap;

import java.util.List;

public class WorldStats {
    private final WorldMap map;
    private final WorldSettings settings;

    public WorldStats(WorldMap map) {
        this.map = map;
        this.settings = map.getSettings();
    }

    public int animalCount() {
        int count = 0;
        for (List<Animal> list: map.getAnimals().values())
            count += list.size();
        return count;
    }

    public int grassCount() {
        return map.getGrass().size();
    }

    public int emptyTiles() {
        return settings.mapWidth()*settings.mapHeight()-map.getAnimals().size();
    }

    public double avgEnergy() {
        double sum = 0;
        double count = 0;
        for (List<Animal> list: map.getAnimals().values()) {
            for (Animal animal: list) {
                sum += animal.getEnergy();
                count++;
            }
        }
        return count!=0 ? sum/count : 0;
    }

    public double avgLifespan() {
        double sum = 0;
        double count = 0;
        for (Animal animal: map.getDead()) {
            sum += animal.getLifespan();
            count++;
        }
        return count!=0 ? sum/count : 0;
    }

    public double avgChildCount() {
        double sum = 0;
        double count = 0;
        for (List<Animal> list: map.getAnimals().values()) {
            for (Animal animal: list) {
                sum += animal.getChildren_count();
                count++;
            }
        }
        return count!=0 ? sum/count : 0;
    }
}
