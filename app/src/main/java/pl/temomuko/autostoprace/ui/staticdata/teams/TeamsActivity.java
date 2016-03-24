package pl.temomuko.autostoprace.ui.staticdata.teams;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.staticdata.StaticDrawerPresenter;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class TeamsActivity extends DrawerActivity {

    @Inject StaticDrawerPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);
        getActivityComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setupUserInfoInDrawer();
        setupToolbarWithToggle();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
