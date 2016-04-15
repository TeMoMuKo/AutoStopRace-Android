package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Szymon Kozak on 2016-04-15.
 */
public class ErrorResponse {

    @SerializedName("errors") private List<String> mErrors;

    public List<String> getErrors() {
        return mErrors;
    }
}
