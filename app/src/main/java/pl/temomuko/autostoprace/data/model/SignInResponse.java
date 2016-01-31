package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by szymen on 2016-01-25.
 */
public class SignInResponse {

    @SerializedName("success") private boolean mSuccess;
    @SerializedName("data") private User mUserData;

    public User getUser() {
        return mUserData;
    }

    public void setUser(User userData) {
        mUserData = userData;
    }
}
