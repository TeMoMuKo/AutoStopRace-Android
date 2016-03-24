package pl.temomuko.autostoprace.util;

import android.location.Address;

/**
 * Created by Szymon Kozak on 2016-03-23.
 */
public final class AddressUtil {

    private AddressUtil() {
        throw new AssertionError();
    }

    public static String getAddressString(Address address) {
        if (address.getMaxAddressLineIndex() == -1) {
            return null;
        }
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i != address.getMaxAddressLineIndex()) {
                addressStringBuilder.append(", ");
            }
        }
        return addressStringBuilder.toString();
    }
}
