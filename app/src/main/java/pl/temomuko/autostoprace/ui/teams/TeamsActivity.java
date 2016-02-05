package pl.temomuko.autostoprace.ui.teams;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by szymen on 2016-02-05.
 */
public class TeamsActivity extends DrawerActivity {

    @Inject TeamsPresenter mTeamsPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);
        getActivityComponent().inject(this);
        mTeamsPresenter.attachView(this);
        mTeamsPresenter.setupUserInfoInDrawer();
        setupToolbarWithToggle();
    }

    @Override
    protected void onDestroy() {
        mTeamsPresenter.detachView();
        super.onDestroy();
    }
}
