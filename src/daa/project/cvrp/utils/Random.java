package daa.project.cvrp.utils;

public class Random {
    
    /**
     * Returns a random integer in range [min, max)
     * 
     * @param min   Minimum number of the range (inclusive)
     * @param max   Maximum number of the range (non inclusive)
     * @return  A random number in range [min, max)
     */
    public static int randomInt(int min, int max) {
        return ((int) (Math.random() * (max - min)) + min);
    }

}
