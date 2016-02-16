package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.util.LogUtil;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public final class DbUtil {

    private DbUtil() {
        throw new AssertionError();
    }

    private final static String DATABASE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final static SimpleDateFormat sDateFormat =
            new SimpleDateFormat(DATABASE_DATE_FORMAT, Locale.US);

    protected static SimpleDateFormat getDateFormat() {
        return sDateFormat;
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static Date getDate(Cursor cursor, String columnName) {
        Date date = new Date();
        String dateString = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        try {
            date = sDateFormat.parse(dateString);
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                LogUtil.wtf("Parsing date failed for values:", dateString);
                e.printStackTrace();
            }
        }
        return date;
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }
}
