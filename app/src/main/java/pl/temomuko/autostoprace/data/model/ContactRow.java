package pl.temomuko.autostoprace.data.model;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactRow {

    private String mType;
    private String mValue;
    private String mOptionalDisplayedValue;
    private String mDescription;

    public ContactRow(String type, String value, String description) {
        mType = type;
        mValue = value;
        mDescription = description;
    }

    public ContactRow(String type, String value, String optionalDisplayedValue, String description) {
        mType = type;
        mValue = value;
        mOptionalDisplayedValue = optionalDisplayedValue;
        mDescription = description;
    }

    public String getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    public String getDisplayedValue() {
        if (mOptionalDisplayedValue == null) {
            return mValue;
        }
        return mOptionalDisplayedValue;
    }

    public String getDescription() {
        return mDescription;
    }
}
