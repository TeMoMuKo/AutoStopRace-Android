package pl.temomuko.autostoprace.data.remote;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import pl.temomuko.autostoprace.Constants;

/**
 * Created by Szymon Kozak on 2016-03-14.
 */
public class GmtDateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext deserializationContext) throws JsonParseException {
        String date = json.getAsString();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.JSON_DATE_FORMAT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            Log.e("Failed to parse date:", e.getMessage());
            return null;
        }
    }
}