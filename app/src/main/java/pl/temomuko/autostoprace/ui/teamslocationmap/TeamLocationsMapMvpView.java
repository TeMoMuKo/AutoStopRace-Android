package pl.temomuko.autostoprace.ui.teamslocationmap;

import java.util.List;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.ui.base.MvpView;

/**
 * Created by Rafał Naniewicz on 03.04.2016.
 */
public interface TeamLocationsMapMvpView extends MvpView {

    void setLocations(List<LocationRecord> locationRecords);

    void showError(String message);
}
