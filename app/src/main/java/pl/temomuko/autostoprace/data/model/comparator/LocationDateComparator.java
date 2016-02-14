package pl.temomuko.autostoprace.data.model.comparator;

import java.util.Comparator;
import java.util.Date;

import pl.temomuko.autostoprace.data.model.Location;

/**
 * Created by szymen on 2016-02-14.
 */
public class LocationDateComparator implements Comparator<Location> {

    @Override
    public int compare(Location location1, Location location2) {
        Date date1 = location1.getServerReceiptDate();
        Date date2 = location2.getServerReceiptDate();
        return date1.before(date2) ? -1 : (date1.after(date2)) ? 1 : 0;
    }
}
