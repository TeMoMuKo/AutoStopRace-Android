package pl.temomuko.autostoprace.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

import pl.temomuko.autostoprace.Constants;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LocationRecord implements Comparable<LocationRecord>, Parcelable {

    @SerializedName("id") private int mId;
    @SerializedName("latitude") private double mLatitude;
    @SerializedName("longitude") private double mLongitude;
    @SerializedName("message") private String mMessage;
    @SerializedName("address") private String mAddress;
    @SerializedName("country") private String mCountry;
    @SerializedName("country_code") private String mCountryCode;
    @SerializedName("created_at") private Date mServerReceiptDate;


    @SerializedName("image")
    @JsonAdapter(LocationImageUriGsonTypeAdapter.class)
    private Uri mImageUri;

    public LocationRecord() {
    }

    public LocationRecord(double latitude, double longitude, String message, String address,
                          String country, String countryCode, Uri imageUri) {
        mLatitude = latitude;
        mLongitude = longitude;
        mMessage = message;
        mAddress = address;
        mCountry = country;
        mCountryCode = countryCode;
        mImageUri = imageUri;
    }

    @Override
    public int compareTo(@NonNull LocationRecord another) {
        int dateCompareResult = getDateCompareResult(another);
        if (dateCompareResult == 0) {
            return Integer.valueOf(another.getId()).compareTo(mId);
        } else {
            return dateCompareResult;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationRecord that = (LocationRecord) o;

        if (mId != that.mId) return false;
        if (Double.compare(that.mLatitude, mLatitude) != 0) return false;
        if (Double.compare(that.mLongitude, mLongitude) != 0) return false;
        if (!mMessage.equals(that.mMessage)) return false;
        if (mAddress != null ? !mAddress.equals(that.mAddress) : that.mAddress != null)
            return false;
        if (mCountry != null ? !mCountry.equals(that.mCountry) : that.mCountry != null)
            return false;
        if (mCountryCode != null ? !mCountryCode.equals(that.mCountryCode) : that.mCountryCode != null)
            return false;
        if (mServerReceiptDate != null ? !mServerReceiptDate.equals(that.mServerReceiptDate) : that.mServerReceiptDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mId;
        temp = Double.doubleToLongBits(mLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + mMessage.hashCode();
        result = 31 * result + (mAddress != null ? mAddress.hashCode() : 0);
        result = 31 * result + (mCountry != null ? mCountry.hashCode() : 0);
        result = 31 * result + (mCountryCode != null ? mCountryCode.hashCode() : 0);
        result = 31 * result + (mServerReceiptDate != null ? mServerReceiptDate.hashCode() : 0);
        return result;
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

    public Uri getImageUri() {
        return mImageUri;
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

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    /* Parcel */

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

    protected LocationRecord(Parcel in) {
        this.mId = in.readInt();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mMessage = in.readString();
        this.mAddress = in.readString();
        this.mCountry = in.readString();
        this.mCountryCode = in.readString();
        long tmpMServerReceiptDate = in.readLong();
        this.mServerReceiptDate = tmpMServerReceiptDate == -1 ? null : new Date(tmpMServerReceiptDate);
        this.mImageUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<LocationRecord> CREATOR = new Creator<LocationRecord>() {
        @Override
        public LocationRecord createFromParcel(Parcel source) {
            return new LocationRecord(source);
        }

        @Override
        public LocationRecord[] newArray(int size) {
            return new LocationRecord[size];
        }
    };

    private static final class LocationImageUriGsonTypeAdapter extends TypeAdapter<Uri> {

        private static final String IMAGE_URL_PREFIX = Constants.API_BASE_URL + "/uploads/location/image/";

        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            out.value(uri == null ? null : uri.toString());
        }

        @Override
        public Uri read(JsonReader in) throws IOException {

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                return Uri.parse(IMAGE_URL_PREFIX + in.nextString());
            }
        }
    }
}
