package pl.temomuko.autostoprace.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class Team implements Comparable<Team> {

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
}
