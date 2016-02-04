package pl.temomuko.autostoprace.ui.base.drawer;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by szymen on 2016-02-03.
 */
public abstract class DrawerActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, DrawerMvpView {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.navigation_view) NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mHeaderUsernameTextView;
    private TextView mHeaderEmailTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mNavigationView.setNavigationItemSelectedListener(this);
        setupHeaderFields();
        setupToolbar();
    }

    protected void setupToolbar() {
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);
    }

    private void setupHeaderFields() {
        View header = mNavigationView.getHeaderView(0);
        mHeaderUsernameTextView = (TextView) header.findViewById(R.id.tv_drawer_username);
        mHeaderEmailTextView = (TextView) header.findViewById(R.id.tv_drawer_email);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void setHeaderUsername(String username) {
        mHeaderUsernameTextView.setText(username);
    }

    @Override
    public void setHeaderEmail(String email) {
        mHeaderEmailTextView.setText(email);
    }
}
