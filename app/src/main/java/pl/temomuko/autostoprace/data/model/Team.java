package pl.temomuko.autostoprace.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class Team implements Comparable<Team>, Parcelable {

    @SerializedName("slug") private String mTeamSlug;
    @SerializedName("name") private String mName;
    @SerializedName("last_location") private LocationRecord mLastLocation;

    @Override
    public int compareTo(@NonNull Team another) {
        return Integer.valueOf(getTeamNumber()).compareTo(another.getTeamNumber());
    }

    public int getTeamNumber() {
        return Integer.valueOf(mTeamSlug.split("-")[1]);
    }

    public String getName() {
        return mName;
    }

    public LocationRecord getLastLocationRecord() {
        return mLastLocation;
    }

    //todo remove legacy constructor

    public Team(String teamSlug, String name, LocationRecord lastLocation) {
        this.mTeamSlug = teamSlug;
        this.mName = name;
        this.mLastLocation = lastLocation;
    }

    /* Parcel */

    protected Team(Parcel in) {
        mTeamSlug = in.readString();
        mName = in.readString();
        mLastLocation = in.readParcelable(LocationRecord.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTeamSlug);
        dest.writeString(mName);
        dest.writeParcelable(mLastLocation, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
}
