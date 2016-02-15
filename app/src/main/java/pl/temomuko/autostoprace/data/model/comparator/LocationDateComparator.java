package pl.temomuko.autostoprace.data.model.comparator;

import java.util.Comparator;
import java.util.Date;

import pl.temomuko.autostoprace.data.model.LocationRecord;

/**
 * Created by szymen on 2016-02-14.
 */
public class LocationDateComparator implements Comparator<LocationRecord> {

    @Override
    public int compare(LocationRecord locationRecord1, LocationRecord locationRecord2) {
        Date date1 = locationRecord1.getServerReceiptDate();
        Date date2 = locationRecord2.getServerReceiptDate();
        return date1.before(date2) ? -1 : (date1.after(date2)) ? 1 : 0;
    }
}
