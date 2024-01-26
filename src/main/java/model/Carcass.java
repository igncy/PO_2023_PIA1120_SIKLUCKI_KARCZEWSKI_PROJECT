package model;

import util.WorldSettings;
import java.security.SecureRandom;

public class Carcass extends AbstractWorldMap {
    private int occupied = 0;
    public Carcass(int ID, WorldSettings settings) {
        super(ID, settings);
    }



    public void generate_carcass(int n){
        int interval = this.Graveyard_list.size();
        SecureRandom rand = new SecureRandom();
        for(int i = 0; i < n; i++){
            while(true)
            {
                int ind = rand.nextInt(interval+1);
                Grass green = Graveyard_list.get(ind);

                if(green.isActive() == true) {
                    green.setActive(false);
                    this.grass.put(green.getPosition(), green);
                    break;
                }
            }
        }
    }

    public void generate_regular_plants(int n)
    {

    }

    @Override
    public void sunrise(){
        generate_carcass(settings.grassCount());
    }

}


