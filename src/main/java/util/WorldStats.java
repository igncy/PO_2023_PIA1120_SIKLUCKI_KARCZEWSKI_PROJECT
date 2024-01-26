package util;

import model.Animal;
import model.WorldMap;

public class WorldStats {
    private final WorldMap map;
    private final WorldSettings settings;

    public WorldStats(WorldMap map) {
        this.map = map;
        this.settings = map.getSettings();
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
}
