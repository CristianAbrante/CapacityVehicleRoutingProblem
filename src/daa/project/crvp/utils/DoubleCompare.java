package daa.project.crvp.utils;

/**
 * Utility class for comparison of doubles
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 22, 2018)
 * @file DoubleCompare.java
 *
 */
public class DoubleCompare {
    
    /** Epsilon: threshold value. We will accept comparisons as true with this as error margin*/
    public static final double EPSILON = 10E-6;
    
    /**
     * Returns whether a is less than b or not.
     * 
     * @param a First value
     * @param b Second value
     * @return Whether a is less than b or not.
     */
    public static boolean lessThan(double a, double b) {
        return (a - b) < -EPSILON;
    }
    
}
