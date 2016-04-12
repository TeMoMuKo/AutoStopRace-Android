package pl.temomuko.autostoprace.ui.teamslocations;

import java.util.List;

import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Rafał Naniewicz on 01.04.2016.
 */
public interface TeamsLocationsMvpView extends DrawerMvpView {

    void setProgress(boolean state);

    void displayTeam(int teamId);

    void setLocations(List<LocationRecord> locationRecords);

    void showError(String message);

    void setHints(List<Team> teams);
}
