package pl.temomuko.autostoprace.ui.base.drawer;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.settings.SettingsActivity;
import pl.temomuko.autostoprace.ui.staticdata.about.AboutActivity;
import pl.temomuko.autostoprace.ui.staticdata.campus.CampusActivity;
import pl.temomuko.autostoprace.ui.staticdata.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.staticdata.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.staticdata.phrasebook.PhrasebookActivity;
import pl.temomuko.autostoprace.ui.staticdata.schedule.ScheduleActivity;
import pl.temomuko.autostoprace.ui.staticdata.teams.TeamsActivity;

/**
 * Created by Szymon Kozak on 2016-02-07.
 */
public class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {

    private static final long DELAY_AFTER_START_CLOSE_DRAWER = 250;

    private final NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private DrawerActivity mCurrentDrawerActivity;

    private static final List<DrawerItemTarget> ACTIVITIES = Arrays.asList(
            new DrawerItemTarget(MainActivity.class, R.id.activity_main),
            new DrawerItemTarget(TeamsActivity.class, R.id.activity_teams),
            new DrawerItemTarget(ScheduleActivity.class, R.id.activity_schedule),
            new DrawerItemTarget(CampusActivity.class, R.id.activity_campus),
            new DrawerItemTarget(PhrasebookActivity.class, R.id.activity_phrasebook),
            new DrawerItemTarget(ContactActivity.class, R.id.activity_contact),
            new DrawerItemTarget(SettingsActivity.class, R.id.activity_settings),
            new DrawerItemTarget(AboutActivity.class, R.id.activity_about)
    );

    public NavigationListener(DrawerActivity activity, DrawerLayout drawerLayout,
                              NavigationView navigationView) {
        mCurrentDrawerActivity = activity;
        mDrawerLayout = drawerLayout;
        mNavigationView = navigationView;
    }

    public void setupMenuElementChecked() {
        clearChecked(ACTIVITIES.size());
        for (int i = 0; i < ACTIVITIES.size(); i++) {
            if (isCurrentActivity(ACTIVITIES.get(i).getActivityClass())) {
                mNavigationView.getMenu().getItem(i).setChecked(true);
            }
        }
    }

    private boolean isCurrentActivity(Class<?> activity) {
        return activity.isInstance(mCurrentDrawerActivity);
    }

    private void clearChecked(int menuSize) {
        for (int i = 0; i < menuSize; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        for (DrawerItemTarget target : ACTIVITIES) {
            if (menuItem.getItemId() == target.getActivityId()) {
                return drawerItemAction(target.getActivityClass());
            }
        }
        return false;
    }

    private boolean drawerItemAction(Class<?> targetActivity) {
        if (isCurrentActivity(targetActivity)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        } else {
            return tryGoToTargetActivity(targetActivity);
        }
    }

    private boolean tryGoToTargetActivity(Class<?> targetActivity) {
        if (isMainActivity(targetActivity)) {
            return tryGoToMain();
        } else {
            openActivity(targetActivity);
            return true;
        }
    }

    private boolean isMainActivity(Class<?> targetActivity) {
        return targetActivity.equals(MainActivity.class);
    }

    private boolean tryGoToMain() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        if (isCurrentActivity(LauncherActivity.class)) {
            showNotLoggedToast();
            return false;
        } else {
            new Handler().postDelayed(mCurrentDrawerActivity::backToMain,
                    DELAY_AFTER_START_CLOSE_DRAWER);
            return true;
        }
    }

    private void showNotLoggedToast() {
        Toast.makeText(mCurrentDrawerActivity,
                R.string.msg_login_to_show_locations, Toast.LENGTH_SHORT).show();
    }

    private void openActivity(Class<?> targetActivity) {
        if (isBackgroundActivity()) goToActivity(targetActivity);
        else goToActivityWithFinishCurrent(targetActivity);
    }

    private boolean isBackgroundActivity() {
        return isCurrentActivity(LauncherActivity.class) || isCurrentActivity(MainActivity.class);
    }

    private void goToActivity(Class targetActivity) {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(mCurrentDrawerActivity, targetActivity);
        new Handler().postDelayed(() -> mCurrentDrawerActivity.startActivity(intent), DELAY_AFTER_START_CLOSE_DRAWER);
    }

    private void goToActivityWithFinishCurrent(Class targetActivity) {
        goToActivity(targetActivity);
        new Handler().postDelayed(mCurrentDrawerActivity::finish, DELAY_AFTER_START_CLOSE_DRAWER);
    }
}
