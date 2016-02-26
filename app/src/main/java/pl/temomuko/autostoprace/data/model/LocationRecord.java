package pl.temomuko.autostoprace.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LocationRecord {

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

    public LocationRecord(double latitude, double longitude, String message, String address) {
        mLatitude = latitude;
        mLongitude = longitude;
        mMessage = message;
        mAddress = address;
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
}
