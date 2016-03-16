package pl.temomuko.autostoprace.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LocationRecord implements Parcelable, Comparable<LocationRecord> {

    @SerializedName("id") private int mId;
    @SerializedName("latitude") private double mLatitude;
    @SerializedName("longitude") private double mLongitude;
    @SerializedName("message") private String mMessage;
    @SerializedName("address") private String mAddress;
    @SerializedName("country") private String mCountry;
    @SerializedName("country_code") private String mCountryCode;
    @SerializedName("created_at") private Date mServerReceiptDate;

    public LocationRecord() {
    }

    public LocationRecord(double latitude, double longitude, String message, String address,
                          String country, String countryCode) {
        mLatitude = latitude;
        mLongitude = longitude;
        mMessage = message;
        mAddress = address;
        mCountry = country;
        mCountryCode = countryCode;
    }

    public int getId() {
        return mId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public Date getServerReceiptDate() {
        return mServerReceiptDate;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setServerReceiptDate(Date serverReceiptDate) {
        mServerReceiptDate = serverReceiptDate;
    }

    public String toString() {
        return "(" + getCountryCode() + ")" + "(" + getLatitude() + ", " + getLongitude() + ",\n" +
                getAddress() + ",\n "
                + getMessage() + ")\n";
    }

    protected LocationRecord(Parcel in) {
        mId = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mMessage = in.readString();
        mAddress = in.readString();
        mCountry = in.readString();
        mCountryCode = in.readString();
        long tmpMServerReceiptDate = in.readLong();
        mServerReceiptDate = tmpMServerReceiptDate != -1 ? new Date(tmpMServerReceiptDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mMessage);
        dest.writeString(mAddress);
        dest.writeString(mCountry);
        dest.writeString(mCountryCode);
        dest.writeLong(mServerReceiptDate != null ? mServerReceiptDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LocationRecord> CREATOR = new Parcelable.Creator<LocationRecord>() {
        @Override
        public LocationRecord createFromParcel(Parcel in) {
            return new LocationRecord(in);
        }

        @Override
        public LocationRecord[] newArray(int size) {
            return new LocationRecord[size];
        }
    };

    @Override
    public int compareTo(@NonNull LocationRecord another) {
        int dateCompareResult = getDateCompareResult(another);
        if (dateCompareResult == 0) {
            return Integer.valueOf(another.getId()).compareTo(mId);
        } else {
            return dateCompareResult;
        }
    }

    private int getDateCompareResult(@NonNull LocationRecord another) {
        int dateCompareResult;
        if (mServerReceiptDate == null && another.getServerReceiptDate() == null) {
            dateCompareResult = 0;
        } else if (mServerReceiptDate == null) {
            dateCompareResult = -1;
        } else if (another.getServerReceiptDate() == null) {
            dateCompareResult = 1;
        } else {
            dateCompareResult = another.getServerReceiptDate().compareTo(mServerReceiptDate);
        }
        return dateCompareResult;
    }
}
