package pl.temomuko.autostoprace.ui.teamslocationsmap;

import java.util.List;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public interface TeamsLocationsMapMvpView extends DrawerMvpView {

    void setAllTeamsProgress(boolean allTeamsProgressState);

    void setTeamProgress(boolean teamProgressState);

    void setHints(List<Team> teams);

    void clearCurrentTeamLocations();

    void setLocations(List<LocationRecord> locationRecords);

    void showError(String message);

    void showInvalidFormatError();

    void showNoLocationRecordsInfo();
}
