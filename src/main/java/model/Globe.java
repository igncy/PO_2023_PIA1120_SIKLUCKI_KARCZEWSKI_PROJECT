package model;


import util.WorldSettings;

import java.security.SecureRandom;

public class Globe extends AbstractWorldMap{

    private int equator_area = 0;
    private boolean[][] vis_equator;



    public Globe(int ID, WorldSettings settings) {
        super(ID, settings);
        calculate_equator();
    }



    public void calculate_equator(){

        int h = this.settings.mapHeight(); int w = this.settings.mapWidth();
        float y1 = (float) 0.4 * h; float y2 = (float) 0.6 * h;
        this.equator = new Boundary(new Vector2d(0, (int)y1) , new Vector2d(w-1, (int)y2 ));
        this.equator_area =  (w) * ((int)y2 - (int)y1 + 1);
        this.vis_equator = new boolean[(int)y2 - (int)y1 + 1][w];

    }

    public void generate_equator_plants(int n)
    {
        this.GreenPlaces_occupied += n;
        int dx = this.settings.mapWidth();
        int dy = this.equator.koniec().getY() - this.equator.start().getY() + 1;
        int xp = 0; int yp = this.equator.start().getY();

        for(int i = 0; i < n; i++){
            while(true){
                SecureRandom rand = new SecureRandom();
                int y1 = rand.nextInt(dy); int x1 = rand.nextInt(dx);
                int x = xp + x1; int y = yp + y1;
                if( !this.vis_GreenPlaces[y][x]){
                    vis_GreenPlaces[y][x] = true;
                    Vector2d cord = new Vector2d(x, y);
                    this.grass.put(cord, new Grass(cord, false) ); // Dla tej mapy true/false nie ma znaczenia
                    break;
                }
            }
        }
    }

    public void generateDesert(int n)
    {
        this.occupiedDesert += n;
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < n; i++){
            int x = rand.nextInt(settings.mapWidth());
            int y = rand.nextInt(settings.mapHeight());
            while(this.vis_GreenPlaces[y][x] || InEquator(new Vector2d(x, y))){
                x = rand.nextInt(settings.mapWidth());
                y = rand.nextInt(settings.mapHeight());
            }
            vis_GreenPlaces[x][y] = true;
            Vector2d cord = new Vector2d(x, y);
            this.grass.put(cord, new Grass(cord, false));
        }
    }

    public void generate_plants(int n)
    {

        SecureRandom rand = new SecureRandom();
        int opt = rand.nextInt(5);
        if(opt != 3)
        {
            if(this.GreenPlaces_occupied + n > this.equator_area){
                generate_equator_plants(this.equator_area - this.GreenPlaces_occupied);
                int res = this.equator_area - n - this.GreenPlaces_occupied;
                generateDesert(Math.min(this.total_area - equator_area - occupiedDesert, res));
            }
            else{
                generate_equator_plants(n);
            }
        }
        else{
            int lim = this.total_area - occupiedDesert - GreenPlaces_occupied;
            generateDesert(Math.min(lim, n));
        }
    }

    @Override
    public void sunrise(){

    }


}

