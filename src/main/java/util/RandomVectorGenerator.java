package util;

import model.Vector2d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomVectorGenerator {
    private final ArrayList<Vector2d> vectors = new ArrayList<>();

    private Vector2d randVector(int lowerLimit, int upperLimit) {
        return new Vector2d(
                ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit),
                ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit)
        );
    }

    public Vector2d genVector(int lowerLimit, int upperLimit) {
        return randVector(lowerLimit, upperLimit);
    }

    public Vector2d genVectorUnique(int lowerLimit, int upperLimit) {
        Vector2d vector = randVector(lowerLimit, upperLimit);
        while (vectors.contains(vector))
            vector = randVector(lowerLimit, upperLimit);
        vectors.add(vector);
        return vector;
    }
}
