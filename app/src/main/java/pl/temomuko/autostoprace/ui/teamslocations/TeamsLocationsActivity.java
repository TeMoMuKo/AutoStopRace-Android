package pl.temomuko.autostoprace.ui.teamslocations;

import android.os.Bundle;
import android.view.Menu;
import android.widget.AutoCompleteTextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.teamslocationmap.TeamLocationsMapFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsLocationsActivity extends DrawerActivity implements TeamsLocationsMvpView {

    private static final String TAG = TeamsLocationsActivity.class.getSimpleName();

    @Inject TeamsLocationsPresenter mTeamsLocationsPresenter;
    private LoadGivenTeam mLoadGivenTeam;
    private Subscription mAutoCompleteSubscription;

    @Bind(R.id.auto_complete_tv_team_number) AutoCompleteTextView mTeamNumberAutocompleteTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_location);
        getActivityComponent().inject(this);
        TeamLocationsMapFragment mapFragment = (TeamLocationsMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_view);
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        }
        mLoadGivenTeam = mapFragment;
        mTeamsLocationsPresenter.attachView(this);
        mTeamsLocationsPresenter.setupUserInfoInDrawer();
        subscribeTeamNumberAutocompleteTextView();
    }

    private void subscribeTeamNumberAutocompleteTextView() {
        mAutoCompleteSubscription = RxTextView.textChanges(mTeamNumberAutocompleteTextView)
                .filter(charSequence -> charSequence.length() > 0)
                .debounce(5, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mTeamsLocationsPresenter::handleTeamCharSequence);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teams_locations_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        mTeamsLocationsPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void displayTeam(int teamId) {
        mLoadGivenTeam.display(teamId);
    }
}
