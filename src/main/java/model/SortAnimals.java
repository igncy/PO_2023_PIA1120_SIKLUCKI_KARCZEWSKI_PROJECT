package model;

import java.util.Comparator;
import java.security.SecureRandom;

public class SortAnimals implements Comparator<Animal> {
    public int compare(Animal z1, Animal z2)
    {
        int E1 = z1.getEnergy(); int E2 = z2.getEnergy();
        int D1 = z1.getDays_of_life(); int D2 = z2.getDays_of_life();
        int comp1 = E1 - E2;
        if(comp1 == 0){

            int comp2 = D1 - D2;

            if(comp2 == 0)
            {
                int comp3 = z1.getChildren_count() - z2.getChildren_count();

                if(comp3 == 0)
                {
                    SecureRandom rand = new SecureRandom();
                    int x = rand.nextInt(2);

                    if(x == 1){ return 1; }
                    else { return -1; }
                }
                else{ return comp3; }
            }
            else{ return comp2; }
        }
        else { return comp1;}
    }
}
