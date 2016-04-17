package pl.temomuko.autostoprace.data.model;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactRow {

    private String mType;
    private String mContent;
    private String mOptionalDisplayedContent;
    private String mDescription;

    public ContactRow(String type, String content, String description) {
        mType = type;
        mContent = content;
        mDescription = description;
    }

    public ContactRow(String type, String content, String optionalDisplayedContent, String description) {
        mType = type;
        mContent = content;
        mOptionalDisplayedContent = optionalDisplayedContent;
        mDescription = description;
    }

    public String getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    public String getDisplayedContent() {
        if (mOptionalDisplayedContent == null) {
            return mContent;
        }
        return mOptionalDisplayedContent;
    }

    public String getDescription() {
        return mDescription;
    }
}
