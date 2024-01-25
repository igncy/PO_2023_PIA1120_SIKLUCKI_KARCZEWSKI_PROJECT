package model;

import java.security.SecureRandom;

public class GrassField extends AbstractWorldMap{
    private int count;

    public  GrassField(int num, int ID) {
        super(ID);
        this.count = num;
        int j = 0;
        while (j * j <= num * 10) {
            j += 1;
        }
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
                actualize_bonds(x, y);
                Vector2d vec = new Vector2d(x, y);
                this.grass.put(vec, new Grass(vec));
            }
        }
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean place(WorldElement stwor) {
        return false;
    }
}

