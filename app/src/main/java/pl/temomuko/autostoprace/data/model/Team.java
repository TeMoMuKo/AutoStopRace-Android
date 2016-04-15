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

    @SerializedName("team_number") private int mTeamNumber;
    @SerializedName("name") private String mName;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @SerializedName("last_location")
    private ArrayList<LocationRecord> mLastLocationRecordList;

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public String getName() {
        return mName;
    }

    public LocationRecord getLastLocationRecord() {
        return mLastLocationRecordList.isEmpty() ? null : mLastLocationRecordList.get(0);
    }

    @Override
    public int compareTo(@NonNull Team another) {
        return Integer.valueOf(mTeamNumber).compareTo(another.mTeamNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTeamNumber);
        dest.writeString(this.mName);
        dest.writeTypedList(mLastLocationRecordList);
    }

    protected Team(Parcel in) {
        this.mTeamNumber = in.readInt();
        this.mName = in.readString();
        this.mLastLocationRecordList = in.createTypedArrayList(LocationRecord.CREATOR);
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
