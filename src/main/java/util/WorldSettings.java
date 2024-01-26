package util;

public record WorldSettings(int mapWidth, int mapHeight,
                            int grassType, int grassCount, int grassEnergy, int grassGrowth,
                            int animalCount, int animalEnergy, int animalSatiety, int animalBreedingEnergy,
                            int mutationType, int mutationMin, int mutationMax,
                            int genomeLength,
                            int sleepTime) {
}
