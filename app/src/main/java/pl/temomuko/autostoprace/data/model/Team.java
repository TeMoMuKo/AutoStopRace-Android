package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by szymen on 2016-01-22.
 */
public class Team {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;
    @SerializedName("location") private Location mLastLocation;
    @SerializedName("users") private List<User> mUsers;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public List<User> getUsers() {
        return mUsers;
    }
}
