package pl.temomuko.autostoprace.util;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rafał Naniewicz on 06.03.2016.
 */
public final class DateUtil {

    private static final SimpleDateFormat DAY_AND_MONTH_SDF = new SimpleDateFormat("d.LL", Locale.getDefault());
    private static final SimpleDateFormat HOUR_SDF = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private DateUtil() {
        throw new AssertionError();
    }

    public static String getDayAndMonthString(@NonNull Date date) {
        return DAY_AND_MONTH_SDF.format(date);
    }

    public static String getTimeString(@NonNull Date date) {
        return HOUR_SDF.format(date);
    }
}
