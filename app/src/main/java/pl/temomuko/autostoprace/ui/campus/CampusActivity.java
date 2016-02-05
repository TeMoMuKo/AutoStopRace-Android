package pl.temomuko.autostoprace.ui.campus;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by szymen on 2016-02-05.
 */
public class CampusActivity extends DrawerActivity implements DrawerMvpView {

    @Inject CampusPresenter mCampusPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus);
        getActivityComponent().inject(this);
        mCampusPresenter.attachView(this);
        mCampusPresenter.setupUserInfoInDrawer();
        setupToolbarWithToggle();
    }

    @Override
    protected void onDestroy() {
        mCampusPresenter.detachView();
        super.onDestroy();
    }
}
