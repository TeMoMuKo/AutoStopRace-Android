package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassResponse {

    @SerializedName("success") private boolean mSuccess;
    @SerializedName("data") private User mUserData;
    @SerializedName("errors") private List<String> mErrors;

    public User getUser() {
        return mUserData;
    }

    public List<String> getErrors() {
        return mErrors != null ? mErrors : new ArrayList<>();
    }
}