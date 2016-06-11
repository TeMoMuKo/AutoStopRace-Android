package pl.temomuko.autostoprace.util;

import android.support.annotation.NonNull;

import java.util.Date;

import pl.temomuko.autostoprace.Constants;

/**
 * Created by Rafał Naniewicz on 06.03.2016.
 */
public final class DateUtil {

    private static final ThreadSafeSimpleDateFormat DAY_AND_MONTH_SDF = new ThreadSafeSimpleDateFormat(
            Constants.APP_DATE_FORMAT);
    private static final ThreadSafeSimpleDateFormat HOUR_SDF = new ThreadSafeSimpleDateFormat(
            Constants.APP_TIME_FORMAT);
    private static final ThreadSafeSimpleDateFormat FULL_DATE_FORMAT = new ThreadSafeSimpleDateFormat(
            Constants.MAP_FULL_DATE_FORMAT);

    private DateUtil() {
        throw new AssertionError();
    }

    public static String getDayAndMonthString(@NonNull Date date) {
        return DAY_AND_MONTH_SDF.format(date);
    }

    public static String getTimeString(@NonNull Date date) {
        return HOUR_SDF.format(date);
    }

    public static String getFullDateMapString(@NonNull Date date) {
        return FULL_DATE_FORMAT.format(date);
    }
}
