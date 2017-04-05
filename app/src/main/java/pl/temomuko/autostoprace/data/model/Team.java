package pl.temomuko.autostoprace.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class Team implements Comparable<Team>, Parcelable {

    @SerializedName("team_slug") private String mTeamSlug;
    @SerializedName("name") private String mName;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @SerializedName("last_location")
    private ArrayList<LocationRecord> mLastLocationRecordList;

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
        return mLastLocationRecordList.isEmpty() ? null : mLastLocationRecordList.get(0);
    }

    /* Parcel */

    protected Team(Parcel in) {
        this.mTeamSlug = in.readString();
        this.mName = in.readString();
        this.mLastLocationRecordList = in.createTypedArrayList(LocationRecord.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTeamSlug);
        dest.writeString(this.mName);
        dest.writeTypedList(mLastLocationRecordList);
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel source) {
            return new Team(source);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };
}
