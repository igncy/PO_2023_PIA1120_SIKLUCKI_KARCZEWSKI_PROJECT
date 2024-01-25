package model;

import util.MapChangeListener;
import util.WorldSettings;

import java.util.HashMap;
import java.util.List;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo, idzik
 */
public interface WorldMap extends MoveValidator {

    /**
     * Place a animal on the map.
     *
     * @param stwor The animal to place on the map.
     * @return True if the animal was placed. The animal cannot be placed if the move is not valid.
     */
    public boolean place(WorldElement stwor);


    /**
     * Moves an animal (if it is present on the map) according to specified direction.
     * If the move is not possible, this method has no effect.
     */
    public boolean isOccupied(Vector2d position);

    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMove since there might be empty positions where the animal
     * cannot move.
     *
     * @param direction Position to check.
     * @return True if the position is occupied.
     */

    /**
     * Return an animal at a given position.
     *
     * @param position The position of the animal.
     * @return animal or null if the position is not occupied.
     */
    List<Animal> objectAt(Vector2d position);
    public String toString();
    public int getID();
    public HashMap<Vector2d, Grass> getGrass();
    public HashMap<Vector2d, List<Animal>> getAnimals();
    public WorldSettings getSettings();

    void addObserver(MapChangeListener observer);
    void removeObserver(MapChangeListener observer);
}

