package model;

import util.WorldSettings;

import java.security.SecureRandom;

public class GrassField extends AbstractWorldMap implements WorldMap {
    private int count;

    public GrassField(int ID, WorldSettings settings) {
        super(ID, settings);
        int num = settings.grassCount();
        this.count = num;
//        int j = 0;
//        while (j * j <= num * 10) {
//            j += 1;
//        }
        int j = Math.min(settings.mapWidth(), settings.mapHeight());
        int[][] coord = new int[num][2];
        boolean[][] select = new boolean[j][j];
        int count = 0;
        SecureRandom rand = new SecureRandom();
        while (count < num) {
            int x = rand.nextInt(j);
            int y = rand.nextInt(j);
            if (!select[x][y]) {
                select[x][y] = true;
                coord[count][0] = x;
                coord[count][1] = y;
                count += 1;
                Vector2d vec = new Vector2d(x, y);
                this.grass.put(vec, new Grass(vec));
            }
        }
    }

    public int getCount() {
        return count;
    }

//    @Override
//    public boolean place(WorldElement stwor) {
//        return false;
//    }
}

