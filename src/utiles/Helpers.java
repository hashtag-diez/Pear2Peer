package utiles;

import java.util.Random;

public class Helpers {
    /**
     * The function returns a random integer between a minimum and maximum value.
     * 
     * @param min The minimum inclusive value that the random number can be.
     * @param max The maximum exclusive value that the random number can take.
     * @return The method is returning a random integer between the minimum and
     *         maximum values
     *         provided.
     */
    public static int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }

    public static int getRandomNumber(int max) {
        return getRandomNumber(0, max);
    }

}
