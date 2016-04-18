package pl.temomuko.autostoprace.util;

/**
 * Created by Szymon Kozak on 2016-04-17.
 */
public final class CoordsUtil {

    private CoordsUtil() {
        throw new AssertionError();
    }

    public static String getDmsTextFromDecimalDegrees(double latitude, double longitude) {
        String latitudeDms = (latitude >= 0 ? "N" : "S") + " " + getConvertedToDms(Math.abs(latitude));
        String longitudeDms = (longitude >= 0 ? "E" : "W") + " " + getConvertedToDms(Math.abs(longitude));
        return latitudeDms + ", " + longitudeDms;
    }

    private static String getConvertedToDms(double decimalDegrees) {
        int degrees = (int) decimalDegrees;
        double degreesDecimalPart = decimalDegrees - degrees;
        double minutesWithSeconds = degreesDecimalPart * 60;
        int minutes = (int) minutesWithSeconds;
        double minutesDecimalPart = minutesWithSeconds - minutes;
        int seconds = (int) Math.round(minutesDecimalPart * 60);
        return degrees + "°" + minutes + "’" + seconds + "”";
    }
}
