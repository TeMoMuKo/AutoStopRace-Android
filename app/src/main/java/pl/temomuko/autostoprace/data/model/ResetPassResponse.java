package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kozak on 2016-03-19.
 */
public class ResetPassResponse {

    @SerializedName("data") private User mUserData;

    public User getUser() {
        return mUserData;
    }

    public void setUser(User user) {
        mUserData = user;
    }
}