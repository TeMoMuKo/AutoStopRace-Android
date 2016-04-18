package pl.temomuko.autostoprace.ui.contact;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ContactRow;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.contact.adapter.AppBarStateChangeListener;
import pl.temomuko.autostoprace.ui.contact.adapter.ContactRowsAdapter;
import pl.temomuko.autostoprace.ui.contact.helper.ContactHandler;
import pl.temomuko.autostoprace.ui.contact.helper.NoIntentHandlerException;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class ContactActivity extends DrawerActivity implements ContactMvpView, ContactRowsAdapter.OnContactRowClickListener {

    private static final String TAG = ContactActivity.class.getSimpleName();

    @Inject ContactPresenter mContactPresenter;
    @Inject ContactRowsAdapter mContactRowsAdapter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecorator;
    @Inject ContactHandler mContactHandler;

    @Bind(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.iv_collapsing_toolbar_background) ImageView mCollapsingTolbarBackground;
    @Bind(R.id.app_bar) AppBarLayout mAppBarLayout;
    @Bind(R.id.rv_contact_rows) RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getActivityComponent().inject(this);
        mContactPresenter.attachView(this);
        mContactPresenter.setupUserInfoInDrawer();
        setupCollapsingToolbar();
        setupRecyclerView();
        mContactPresenter.loadContactRows();
    }

    private void setupCollapsingToolbar() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener(mCollapsingToolbarLayout) {
            @Override
            public void onStateExpanded() {
                getDrawerToggle().setDrawerIndicatorEnabled(true);
                mCollapsingToolbarLayout.setTitle(getString(R.string.title_activity_contact));
            }

            @Override
            public void onStateCollapsed() {
                getDrawerToggle().setDrawerIndicatorEnabled(false);
                mCollapsingToolbarLayout.setTitle(null);
            }
        });
        Picasso.with(this).load(R.drawable.img_team_asr)
                .placeholder(R.drawable.img_team_asr_placeholder)
                .noFade()
                .fit()
                .centerCrop()
                .into(mCollapsingTolbarBackground);
    }

    private void setupRecyclerView() {
        mContactRowsAdapter.setOnContactRowClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mContactRowsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.addItemDecoration(mVerticalDividerItemDecorator);
    }

    @Override
    protected void onDestroy() {
        mContactPresenter.detachView();
        super.onDestroy();
    }

    /* MVP View methods */

    @Override
    public void setContactRows(List<ContactRow> contactRows) {
        mContactRowsAdapter.updateContactRows(contactRows);
    }

    /* Contact row listener methods */

    @Override
    public void onContactRowClick(String type, String value) {
        try {
            mContactHandler.startIntent(type, value);
        } catch (NoIntentHandlerException e) {
            showNoIntentHandlerError();
        }
    }

    private void showNoIntentHandlerError() {
        Toast.makeText(this, R.string.error_action_cant_be_handled, Toast.LENGTH_SHORT).show();
    }
}
