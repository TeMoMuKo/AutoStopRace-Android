package pl.temomuko.autostoprace.ui.teamslocationsmap;

import android.net.Uri;

import java.util.List;

import pl.temomuko.autostoprace.data.local.LocationsViewMode;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;
import pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.wall.WallItem;

public interface TeamsLocationsMapMvpView extends DrawerMvpView {

    void setAllTeamsProgress(boolean allTeamsProgressState);

    void setTeamProgress(boolean teamProgressState);

    void setHints(List<Team> teams);

    void clearCurrentTeamLocations();

    void setLocationsForMap(List<LocationRecord> locationRecords);

    void setWallItems(List<WallItem> wallItems);

    void showError(String message);

    void showInvalidFormatError();

    void showNoLocationRecordsInfoForMap();

    void hideWallItems();

    void openFullscreenImage(Uri imageUri);

    void setLocationsViewMode(LocationsViewMode locationsViewMode);

    void setWallVisible(boolean visible);
}
