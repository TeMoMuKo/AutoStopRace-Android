package pl.temomuko.autostoprace.domain.model;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactField {

    private final String mType;
    private final String mValue;
    private String mOptionalDisplayedValue;
    private final String mDescription;

    public ContactField(String type, String value, String description) {
        mType = type;
        mValue = value;
        mDescription = description;
    }

    public ContactField(String type, String value, String optionalDisplayedValue, String description) {
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
