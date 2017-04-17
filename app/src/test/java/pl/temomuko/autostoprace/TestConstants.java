package pl.temomuko.autostoprace;


import android.net.Uri;

import pl.temomuko.autostoprace.data.model.LocationRecord;

public final class TestConstants {

    public static final Uri TEST_IMAGE_URI = Uri.parse("http://random.cat/i/OoAEJ.jpg");

    public static final LocationRecord PROPER_LOCATION_RECORD = new LocationRecord(18.05, 17.17, "Yo", "Somewhere, Poland", "Poland", "PL", TEST_IMAGE_URI);


    private TestConstants() {
        throw new AssertionError();
    }

}
