package daa.project.crvp.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DoubleFormatter {
    /** Maximum number of digits to display for doubles */
    public static final int MAXIMUM_DECIMAL_DIGITS_TO_DISPLAY = 5;
    
    /** Formatter */
    private static final DecimalFormat FORMATTER = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    
    public static String format(double numberToFormat) {
        FORMATTER.setMaximumFractionDigits(MAXIMUM_DECIMAL_DIGITS_TO_DISPLAY);
        return FORMATTER.format(numberToFormat);
    }
}
