package pl.temomuko.autostoprace.ui.staticdata.contact;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class ContactActivity extends DrawerActivity {

    @Inject ContactPresenter mContactPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getActivityComponent().inject(this);
        mContactPresenter.attachView(this);
        mContactPresenter.setupUserInfoInDrawer();
        setupToolbarWithToggle();
    }

    @Override
    protected void onDestroy() {
        mContactPresenter.detachView();
        super.onDestroy();
    }
}
