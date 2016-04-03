package pl.temomuko.autostoprace.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rafał on 29.03.2016.
 */

public class ThreadSafeSimpleDateFormat {

    private ThreadLocal<SimpleDateFormat> mSimpleDateFormatThreadLocal;

    public ThreadSafeSimpleDateFormat(final String format) {
        mSimpleDateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(format, Locale.getDefault());
            }
        };
    }

    public String format(Date date) {
        return mSimpleDateFormatThreadLocal.get().format(date);
    }

    public Date parse(String dateString) throws ParseException {
        return mSimpleDateFormatThreadLocal.get().parse(dateString);
    }
}