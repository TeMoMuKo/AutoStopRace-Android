package pl.temomuko.autostoprace.ui.base.drawer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-02-03.
 */

public abstract class DrawerActivity extends BaseActivity implements DrawerMvpView {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;

    protected ActionBarDrawerToggle mDrawerToggle;
    private NavigationListener mNavigationListener;
    private TextView mHeaderUsernameTextView;
    private TextView mHeaderEmailTextView;
    private TextView mTeamTextView;

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationListener.setupMenuElementChecked();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mNavigationListener = new NavigationListener(this, mDrawerLayout, mNavigationView);
        setupHeaderFields();
        setupToolbarWithToggle();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    protected void setupToolbarWithToggle() {
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.setDrawerSlideAnimationEnabled(false);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void setupHeaderFields() {
        View header = mNavigationView.getHeaderView(0);
        mHeaderUsernameTextView = (TextView) header.findViewById(R.id.tv_drawer_username);
        mHeaderEmailTextView = (TextView) header.findViewById(R.id.tv_drawer_email);
        mTeamTextView = (TextView) header.findViewById(R.id.tv_team_number);
    }

    /* MVP View methods */

    @Override
    public void setupHeaderUsername(String username) {
        mHeaderUsernameTextView.setText(username);
    }

    @Override
    public void setupHeaderEmail(String email) {
        mHeaderEmailTextView.setText(email);
    }

    @Override
    public void setupTeamNumberText(int teamNumber) {
        ((LinearLayout) mTeamTextView.getParent()).setVisibility(View.VISIBLE);
        mTeamTextView.setText(String.valueOf(teamNumber));
    }
}
