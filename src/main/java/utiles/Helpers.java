package main.java.utiles;

import java.util.Collection;
import java.util.Random;

public class Helpers {
    protected static Random r = new Random();
    public static final String GLOBAL_CLOCK_URI = "my-clock-uri";

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
        return r.nextInt(max - min) + min;
    }

    public static int getRandomNumber(int max) {
        return getRandomNumber(0, max);
    }

    // Get a random element from an set
    synchronized public static <T> T getRandomElement(Collection<T> set) {
        int size = set.size();
        int item = getRandomNumber(size);
        int i = 0;
        for (T obj : set) {
            if (i == item)
                return obj;

            i++;
        }
        return null;
    }

    synchronized public static <T> T popRandomElement(Collection<T> set) {
        T obj = getRandomElement(set);
        set.remove(obj);
        return obj;
    }

    // Get a random sub set from an set
    synchronized public static <T> Collection<T> getRandomCollection(Collection<T> set, int size) {
        // Copy collection
        Collection<T> subSet = new java.util.ArrayList<T>(set);
        while (subSet.size() > size) {
            subSet.remove(getRandomElement(subSet));
        }
        return subSet;
    }
}
