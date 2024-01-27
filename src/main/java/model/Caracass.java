package model;

import java.security.SecureRandom;
import util.WorldSettings;
import java.security.SecureRandom;




public class Caracass extends AbstractWorldMap {
    private int occupiedCaracass = 0;

    private boolean[][] vis_GreenPlaces;

    public Caracass(int ID, WorldSettings settings) {
        super(ID, settings);
        int W = settings.mapWidth(); int H = settings.mapHeight();
        vis_GreenPlaces = new boolean[H][W];
    }

    public void generate_caracass(int n){

        this.occupiedCaracass += n;
        int interval = this.Graveyard_list.size();
        SecureRandom rand = new SecureRandom();
        for(int i = 0; i < n; i++){
            while(true)
            {
                int ind = rand.nextInt(interval);
                Grass green = Graveyard_list.get(ind);

                if(green.isActive() == true) {
                    green.setActive(false);
                    this.grass.put(green.getPosition(), green);
                    break;
                }
            }
        }
    }

    public void generate_rest(int n)
    {
        SecureRandom rand2 = new SecureRandom();
        for (int i = 0; i < n; i++){
            while(true) {
                int x1 = rand2.nextInt(settings.mapWidth());
                int y1 = rand2.nextInt(settings.mapHeight());
                Vector2d pos1 = new Vector2d(x1, y1);
                if (!vis_GreenPlaces[y1][x1] && !this.Graveyard_set.contains(pos1)) {
                    vis_GreenPlaces[y1][x1] = true;
                    grass.put(pos1, new Grass(pos1, true));
                }
            }
        }
    }


    public void create_flora(int n)
    {
        SecureRandom rand = new SecureRandom();
        int option = rand.nextInt(5);
        if(option == 0){
            if(this.occupiedCaracass + n > this.Graveyard_list.size()){
                generate_caracass(this.Graveyard_list.size() - this.occupiedCaracass);
            }
            else{
                generate_caracass(n);
            }
        }
        else{

        }
    }

    @Override
    public void sunrise(){
        create_flora(settings.grassCount());
    }

}

