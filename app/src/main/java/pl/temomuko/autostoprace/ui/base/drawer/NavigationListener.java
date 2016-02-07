package pl.temomuko.autostoprace.ui.base.drawer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

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
 * Created by szymen on 2016-02-07.
 */
public class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {

    private final NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Context mContext;

    public static final ImmutableMap<Class<? extends BaseActivity>, Integer> ACTIVITIES =
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

    public NavigationListener(Context context, DrawerLayout drawerLayout,
                              NavigationView navigationView) {
        mContext = context;
        mDrawerLayout = drawerLayout;
        mNavigationView = navigationView;
    }

    public void setupMenuElementChecked() {
        int i = 0;
        clearChecked(ACTIVITIES.size());
        for (Map.Entry<Class<? extends BaseActivity>, Integer> activityElement : ACTIVITIES.entrySet()) {
            if (isCurrentActivity(activityElement.getKey())) {
                mNavigationView.getMenu().getItem(i).setChecked(true);
            }
            i++;
        }
    }

    private boolean isCurrentActivity(Class<?> activity) {
        return activity.isInstance(mContext);
    }

    private void clearChecked(int menuSize) {
        for (int i = 0; i < menuSize; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        for (Map.Entry<Class<? extends BaseActivity>, Integer> activityElement : ACTIVITIES.entrySet()) {
            if (menuItem.getItemId() == activityElement.getValue()) {
                return activityAction(activityElement.getKey());
            }
        }
        return false;
    }

    private boolean activityAction(Class<?> targetActivity) {
        if (isCurrentActivity(targetActivity)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            tryGoToTargetActivity(targetActivity);
        }
        return true;
    }

    private void tryGoToTargetActivity(Class<?> targetActivity) {
        if (targetActivity.equals(MainActivity.class)) {
            if (isCurrentActivity(LauncherActivity.class)) {
                goToActivity(targetActivity, true);
                Toast.makeText(mContext, R.string.msg_login_to_show_locations, Toast.LENGTH_SHORT).show();
            } else ((DrawerActivity) mContext).backToMain();
        } else {
            if (isCurrentActivity(LauncherActivity.class)) goToActivity(targetActivity, false);
            else goToActivity(targetActivity, true);
        }
    }

    private void goToActivity(Class targetActivity, boolean removeFromStack) {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(mContext, targetActivity);
        new Handler().postDelayed(() -> {
            mContext.startActivity(intent);
            if (!(mContext instanceof MainActivity)) {
                if (removeFromStack) ((DrawerActivity) mContext).finish();
            }
        }, 300);
    }
}
