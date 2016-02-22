package pl.temomuko.autostoprace.ui.phrasebook;

import android.os.Bundle;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class PhrasebookActivity extends DrawerActivity {

    @Inject PhrasebookPresenter mPhrasebookPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrasebook);
        getActivityComponent().inject(this);
        mPhrasebookPresenter.attachView(this);
        mPhrasebookPresenter.setupUserInfoInDrawer();
        setupToolbarWithToggle();
    }

    @Override
    protected void onDestroy() {
        mPhrasebookPresenter.detachView();
        super.onDestroy();
    }
}
