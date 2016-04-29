package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class User {

    @SerializedName("id") private int mId;
    @SerializedName("team_number") private int mTeamNumber;
    @SerializedName("first_name") private String mFirstName;
    @SerializedName("last_name") private String mLastName;
    @SerializedName("email") private String mEmail;

    public User(int id, int teamNumber, String firstName, String lastName, String email) {
        mId = id;
        mTeamNumber = teamNumber;
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
    }

    public int getId() {
        return mId;
    }

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public String getFirstName() {
        return mFirstName.trim();
    }

    public String getLastName() {
        return mLastName.trim();
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUsername() {
        return getFirstName().concat(" ").concat(getLastName());
    }
}
