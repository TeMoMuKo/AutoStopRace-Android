package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class Team {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @SerializedName("last_location")
    private ArrayList<LocationRecord> mLastLocationRecordList;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public LocationRecord getLastLocationRecord() {
        return mLastLocationRecordList.isEmpty() ? null : mLastLocationRecordList.get(0);
    }
}
