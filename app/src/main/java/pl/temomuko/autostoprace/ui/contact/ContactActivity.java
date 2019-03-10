package pl.temomuko.autostoprace.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.domain.model.ContactField;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.contact.adapter.ContactRowsAdapter;
import pl.temomuko.autostoprace.ui.contact.helper.ContactHandler;
import pl.temomuko.autostoprace.ui.contact.helper.NoIntentHandlerException;
import pl.temomuko.autostoprace.ui.widget.CustomContactCollapsingToolbarLayout;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class ContactActivity extends DrawerActivity implements ContactMvpView {

    @Inject ContactPresenter mContactPresenter;
    @Inject ContactRowsAdapter mContactRowsAdapter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecorator;
    @Inject ContactHandler mContactContactHandler;

    @BindView(R.id.collapsing_toolbar_layout) CustomContactCollapsingToolbarLayout mCustomContactCollapsingToolbarLayout;
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.iv_collapsing_toolbar_background) ImageView mCollapsingToolbarBackground;
    @BindView(R.id.fab_contact_action) FloatingActionButton mFabContactAction;
    @BindView(R.id.rv_contact_rows) RecyclerView mRecyclerView;

    public static void start(Context context) {
        Intent starter = new Intent(context, ContactActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getActivityComponent().inject(this);
        mContactPresenter.attachView(this);
        mContactPresenter.setupUserInfoInDrawer();
        setupCollapsingToolbar();
        setupRecyclerView();
        setListeners();
        mContactPresenter.loadContactData();
    }

    @Override
    protected void onDestroy() {
        mContactPresenter.detachView();
        super.onDestroy();
    }

    private void setupCollapsingToolbar() {
        Glide.with(this).load(R.drawable.img_team_asr_2018)
                .placeholder(R.drawable.img_team_asr_placeholder_2018)
                .centerCrop()
                .dontAnimate()
                .into(mCollapsingToolbarBackground);
    }

    private void setupRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mContactRowsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.addItemDecoration(mVerticalDividerItemDecorator);
    }

    private void setListeners() {
        mContactRowsAdapter.setOnContactRowClickListener((type, value) -> {
            try {
                mContactContactHandler.startIntent(type, value);
            } catch (NoIntentHandlerException e) {
                showNoIntentHandlerError();
            }
        });
    }

    /* MVP View methods */

    @Override
    public void setContactRows(List<ContactField> contactFields) {
        mContactRowsAdapter.updateContactRows(contactFields);
    }

    @Override
    public void setUpFab(ContactField fabContactField) {
        mFabContactAction.setVisibility(View.VISIBLE);
        mFabContactAction.setImageResource(ContactHandler.getIcon(fabContactField.getType()));
        mFabContactAction.setOnClickListener(view -> {
            try {
                mContactContactHandler.startIntent(fabContactField.getType(), fabContactField.getValue());
            } catch (NoIntentHandlerException e) {
                showNoIntentHandlerError();
            }
        });
    }

    /* Private helper methods */

    private void showNoIntentHandlerError() {
        Toast.makeText(this, R.string.error_action_cant_be_handled, Toast.LENGTH_SHORT).show();
    }
}
