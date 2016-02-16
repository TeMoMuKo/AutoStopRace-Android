package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szymen on 2016-01-25.
 */
public class SignInResponse {

    @SerializedName("success") private boolean mSuccess;
    @SerializedName("data") private User mUserData;
    @SerializedName("errors") private List<String> mErrors;

    public User getUser() {
        return mUserData;
    }

    public void setUser(User userData) {
        mUserData = userData;
    }

    public List<String> getErrors() {
        return mErrors != null ? mErrors : new ArrayList<>();
    }
}
