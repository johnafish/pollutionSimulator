package pollutionsimulator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author John Fish <john@johnafish.ca>
 */

public class PollutionSimulator {
    int width = 400;
    int height = 400;
    double[][] peopleValues = new double[width][height];
    double[][] atmosphericValues = new double[width][height];
    
    public void setInitial(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                peopleValues[i][j] = ThreadLocalRandom.current().nextDouble(0.5, 1.5);
                atmosphericValues[i][j] = 1;
            }
        }
    }
    
    public static void main(String[] args) {

    }

}
