package pl.temomuko.autostoprace.data.local.database;

import android.database.Cursor;

import java.text.ParseException;
import java.util.Date;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.util.LogUtil;
import pl.temomuko.autostoprace.util.ThreadSafeSimpleDateFormat;

/**
 * Created by Rafał Naniewicz on 04.02.2016.
 */
public final class DbUtil {

    private DbUtil() {
        throw new AssertionError();
    }

    private final static ThreadSafeSimpleDateFormat sThreadSafeSimpleDateFormat =
            new ThreadSafeSimpleDateFormat(Constants.DATABASE_DATE_FORMAT);

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static Date getDate(Cursor cursor, String columnName) {
        Date date;
        String dateString = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        try {
            date = sThreadSafeSimpleDateFormat.parse(dateString);
            return date;
        } catch (ParseException e) {
            LogUtil.wtf("Parsing date failed for values:", dateString);
            e.printStackTrace();
        }
        return null;
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }

    public static String formatDate(Date date) {
        return sThreadSafeSimpleDateFormat.formatDate(date);
    }
}
