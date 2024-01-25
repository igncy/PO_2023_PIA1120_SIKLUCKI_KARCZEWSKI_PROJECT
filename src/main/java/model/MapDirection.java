package model;

public enum MapDirection {
    NORTH, NORTHWEST, NORTHEAST, EAST, WEST, SOUTHEAST, SOUTHWEST, SOUTH;
    public MapDirection next (){
        MapDirection nowy = switch(this){
            case NORTH -> NORTHEAST;
            case NORTHEAST -> EAST;
            case EAST -> SOUTHEAST;
            case SOUTHEAST -> SOUTH;
            case SOUTH -> SOUTHWEST;
            case SOUTHWEST -> WEST;
            case WEST -> NORTHWEST;
            case NORTHWEST -> NORTH;
            default -> NORTH;
        };
        return nowy;
    }

    public MapDirection previous(){
        MapDirection res = switch(this){
            case NORTH -> NORTHWEST;
            case NORTHEAST -> NORTH;
            case EAST -> NORTHEAST;
            case SOUTHEAST -> EAST;
            case SOUTH -> SOUTHEAST;
            case SOUTHWEST -> WEST;
            case WEST -> SOUTHWEST;
            case NORTHWEST -> WEST;
        };
        return res;

    }


    public Vector2d toUnitVector(){
        Vector2d unit = switch(this){
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
        return unit;
    }

    @Override
    public String toString() {
        String conv = switch (this) {
            case EAST -> "Wschod";
            case NORTHEAST -> "Polnocny wschod";
            case SOUTHEAST -> "Poludniowy wschod";
            case NORTH -> "Polnoc";
            case WEST -> "Zachod";
            case NORTHWEST -> "Polnocny zachod";
            case SOUTHWEST -> "Poludniowy zachod";
            case SOUTH -> "Wschod";
            default -> "Polnoc";
        };
        return conv;
    }

}
