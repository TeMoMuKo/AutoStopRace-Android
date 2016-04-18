package pl.temomuko.autostoprace.ui.staticdata.partners;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;
import pl.temomuko.autostoprace.ui.staticdata.StaticDrawerPresenter;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class PartnersActivity extends DrawerActivity implements DrawerMvpView {

    @Inject StaticDrawerPresenter mPresenter;
    @Inject PartnersAdapter mPartnersAdapter;
    @Bind(R.id.rv_partners) RecyclerView mPartnersRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partners);
        getActivityComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setupUserInfoInDrawer();
        setupPartnersRecyclerView();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    private void setupPartnersRecyclerView() {
        mPartnersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPartnersRecyclerView.setAdapter(mPartnersAdapter);
    }
}
