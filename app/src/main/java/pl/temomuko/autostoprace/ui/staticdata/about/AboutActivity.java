package pl.temomuko.autostoprace.ui.staticdata.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.util.IntentUtil;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.btn_go_to_store) Button mGoToStoreButton;
    @BindView(R.id.tv_about_app) TextView mAboutAppTextView;
    @BindView(R.id.tv_authors) TextView mAuthorsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mAboutAppTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mAuthorsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        setupToolbarWithBack();
        setupButtonsTextStyleOnPreLollipop();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareApp();
                return true;
            case R.id.action_licenses:
                startLicensesActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupButtonsTextStyleOnPreLollipop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mGoToStoreButton.setTypeface(null, Typeface.BOLD);
        }
    }

    private void setListeners() {
        mGoToStoreButton.setOnClickListener(v -> goToStore());
    }

    private void goToStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        IntentUtil.addClearBackStackIntentFlags(intent);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent playStoreWebPageIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.GOOGLE_PLAY_BASE_URL + getPackageName()));
            IntentUtil.addClearBackStackIntentFlags(playStoreWebPageIntent);
            if (isAppForIntentAvailable(playStoreWebPageIntent)) {
                startActivity(playStoreWebPageIntent);
            }
        }
    }

    private boolean isAppForIntentAvailable(Intent playStoreWebPageIntent) {
        return playStoreWebPageIntent.resolveActivity(getPackageManager()) != null;
    }

    private void shareApp() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.GOOGLE_PLAY_BASE_URL + getPackageName());
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    private void startLicensesActivity() {
        Intent intent = new Intent(this, LicensesActivity.class);
        startActivity(intent);
    }
}
