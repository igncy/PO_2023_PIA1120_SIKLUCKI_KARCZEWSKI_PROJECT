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

    public int avgEnergy() {
        return 0;
    }

    public int avgLifespan() {
        return 0;
    }

    public int avgChildCount() {
        return 0;
    }
}
