package pl.temomuko.autostoprace.ui.base.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.about.AboutActivity;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.campus.CampusActivity;
import pl.temomuko.autostoprace.ui.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.phrasebook.PhrasebookActivity;
import pl.temomuko.autostoprace.ui.schedule.ScheduleActivity;
import pl.temomuko.autostoprace.ui.settings.SettingsActivity;
import pl.temomuko.autostoprace.ui.teams.TeamsActivity;

/**
 * Created by szymen on 2016-02-03.
 */

public abstract class DrawerActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, DrawerMvpView {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view) NavigationView mNavigationView;
    protected ActionBarDrawerToggle mDrawerToggle;
    private TextView mHeaderUsernameTextView;
    private TextView mHeaderEmailTextView;
    private TeamCircleView mTeamCircleView;

    private static final ImmutableMap<Class<? extends BaseActivity>, Integer> ACTIVITIES =
            new ImmutableMap.Builder<Class<? extends BaseActivity>, Integer>()
                    .put(MainActivity.class, R.id.activity_main)
                    .put(TeamsActivity.class, R.id.activity_teams)
                    .put(ScheduleActivity.class, R.id.activity_schedule)
                    .put(CampusActivity.class, R.id.activity_campus)
                    .put(PhrasebookActivity.class, R.id.activity_phrasebook)
                    .put(ContactActivity.class, R.id.activity_contact)
                    .put(SettingsActivity.class, R.id.activity_settings)
                    .put(AboutActivity.class, R.id.activity_about)
                    .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMenuElementChecked();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    protected void setupToolbar() {
        setSupportActionBar(mToolbar);
    }

    protected void setupToolbarWithToggle() {
        setupToolbar();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
    }

    private void setupMenuElementChecked() {
        int i = 0;
        clearChecked(ACTIVITIES.size());
        for (Map.Entry<Class<? extends BaseActivity>, Integer> entry : ACTIVITIES.entrySet()) {
            if (entry.getKey().isInstance(this)) {
                mNavigationView.getMenu().getItem(i).setChecked(true);
            }
            i++;
        }
    }

    private void clearChecked(int menuSize) {
        for (int i = 0; i < menuSize; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mNavigationView.setNavigationItemSelectedListener(this);
        setupHeaderFields();
        setupToolbar();
    }

    private void setupHeaderFields() {
        View header = mNavigationView.getHeaderView(0);
        mHeaderUsernameTextView = (TextView) header.findViewById(R.id.tv_drawer_username);
        mHeaderEmailTextView = (TextView) header.findViewById(R.id.tv_drawer_email);
        mTeamCircleView = (TeamCircleView) header.findViewById(R.id.team_circle_view);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        for (Map.Entry<Class<? extends BaseActivity>, Integer> entry : ACTIVITIES.entrySet()) {
            if (item.getItemId() == entry.getValue()) {
                return activityAction(entry.getKey());
            }
        }
        return false;
    }

    private boolean activityAction(Class<?> targetActivity) {
        if (targetActivity.isInstance(this)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            tryGoToTargetActivity(targetActivity);
        }
        return true;
    }

    private void tryGoToTargetActivity(Class<?> targetActivity) {
        if (targetActivity.equals(MainActivity.class)) {
            if (this instanceof LauncherActivity) {
                goToActivity(targetActivity, true);
                Toast.makeText(this, R.string.msg_login_to_show_locations, Toast.LENGTH_SHORT).show();
            } else super.onBackPressed();
        } else {
            if (this instanceof LauncherActivity) goToActivity(targetActivity, false);
            else goToActivity(targetActivity, true);
        }
    }

    private void goToActivity(Class targetActivity, boolean removeFromStack) {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, targetActivity);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            if (!(this instanceof MainActivity)) {
                if (removeFromStack) finish();
            }
        }, 300);
    }

    @Override
    public void setupHeaderUsername(String username) {
        mHeaderUsernameTextView.setText(username);
    }

    @Override
    public void setupHeaderEmail(String email) {
        mHeaderEmailTextView.setText(email);
    }

    @Override
    public void setupTeamCircle(int teamId) {
        mTeamCircleView.setTeamId(teamId);
    }
}
