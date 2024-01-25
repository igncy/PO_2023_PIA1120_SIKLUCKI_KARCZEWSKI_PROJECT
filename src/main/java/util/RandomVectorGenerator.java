package util;

import model.Vector2d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomVectorGenerator {
    private final ArrayList<Vector2d> vectors = new ArrayList<>();

    public Vector2d genVector(Vector2d lower, Vector2d upper) {
        return new Vector2d(
                ThreadLocalRandom.current().nextInt(lower.getX(), upper.getX()),
                ThreadLocalRandom.current().nextInt(lower.getY(), upper.getY())
        );
    }

    public Vector2d genVectorUnique(Vector2d lower, Vector2d upper) {
        Vector2d vector = genVector(lower, upper);
        while (vectors.contains(vector))
            vector = genVector(lower, upper);
        vectors.add(vector);
        return vector;
    }
}
