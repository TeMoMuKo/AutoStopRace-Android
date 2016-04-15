package pl.temomuko.autostoprace.ui.staticdata.campus;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.staticdata.StaticDrawerPresenter;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class CampusActivity extends DrawerActivity {

    @Inject StaticDrawerPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus);
        getActivityComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setupUserInfoInDrawer();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
