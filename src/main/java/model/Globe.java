package model;


import util.WorldSettings;

import java.security.SecureRandom;

public class Globe extends AbstractWorldMap{

    private int occupiedEquator = 0;
    private int equator_area = 0;
    private boolean[][] vis_equator;

    private Boundary equator;

    public Globe(int ID, WorldSettings settings) {
        super(ID, settings);
    }

    public void calculate_equator(){

        int h = this.settings.mapHeight(); int w = this.settings.mapWidth()-1;
        float y1 = (float) 0.4 * h; float y2 = (float) 0.6 * h;
        this.equator = new Boundary(new Vector2d(0, (int)y1) , new Vector2d(w, (int)y2 ));
        this.equator_area =  (w) * ((int)y2 - (int)y1 + 1);
        this.vis_equator = new boolean[(int)y2 - (int)y1 + 1][w];

    }

    public void generate_equator_plants(int n)
    {
        this.occupiedEquator += n;

        int area = equator_area;
        int dx = this.settings.mapWidth();
        int dy = this.equator.koniec().getY() - this.equator.start().getY() + 1;
        int xp = 0; int yp = this.equator.start().getY();

        for(int i = 0; i < n; i++){
            while(true){
                SecureRandom rand = new SecureRandom();
                int y1 = rand.nextInt(dy); int x1 = rand.nextInt(dx);
                if(!this.vis_equator[y1][x1]){
                    vis_equator[y1][x1] = true;
                    Vector2d cord = new Vector2d(x1 + xp, y1 + yp);
                    this.grass.put(cord, new Grass(cord, false) );
                    break;
                }
            }
        }
    }


}

