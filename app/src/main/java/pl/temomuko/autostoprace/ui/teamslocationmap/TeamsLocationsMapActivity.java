package pl.temomuko.autostoprace.ui.teamslocationmap;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsLocationsMapActivity extends DrawerActivity implements TeamsLocationsMapMvpView {

    @Inject TeamsLocationsMapPresenter mTeamsLocationsMapPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);
        getActivityComponent().inject(this);
        mTeamsLocationsMapPresenter.attachView(this);
        mTeamsLocationsMapPresenter.setupUserInfoInDrawer();
    }

    @Override
    protected void onDestroy() {
        mTeamsLocationsMapPresenter.detachView();
        super.onDestroy();
    }
}
