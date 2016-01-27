package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by szymen on 2016-01-22.
 */
public class User {

    @SerializedName("id") private int mId;
    @SerializedName("team_id") private int mTeamId;
    @SerializedName("first_name") private String mFirstName;
    @SerializedName("last_name") private String mLastName;
    @SerializedName("email") private String mEmail;

    public User(int id, int teamId, String firstName, String lastName, String email) {
        mId = id;
        mTeamId = teamId;
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
    }

    public int getId() {
        return mId;
    }

    public int getTeamId() {
        return mTeamId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }
}
