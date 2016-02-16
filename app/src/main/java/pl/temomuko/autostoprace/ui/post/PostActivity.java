package pl.temomuko.autostoprace.ui.post;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by szymen on 2016-01-30.
 */
public class PostActivity extends BaseActivity implements PostMvpView {

    @Inject PostPresenter mPostPresenter;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.et_message) EditText mMessageEditText;
    @Bind(R.id.tv_current_location_cords) TextView mCurrentLocationCordsTextView;
    @Bind(R.id.tv_current_location_adress) TextView mCurrentLocationAddressTextView;
    private boolean mIsResolutionShown = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getActivityComponent().inject(this);
        mPostPresenter.attachView(this);
        mPostPresenter.setupCurrentLocation();
        setupToolbarWithBack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPostPresenter.startLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPostPresenter.stopLocationService();
    }

    @Override
    protected void onDestroy() {
        mPostPresenter.detachView();
        super.onDestroy();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post_send:
                String message = mMessageEditText.getText().toString().trim();
                mPostPresenter.saveLocation(message);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPostPresenter.handleActivityResult(requestCode, resultCode);
    }

    /* MVP View methods */

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showSuccessInfo() {
        Toast.makeText(this, R.string.saved_location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCurrentLocationCords(double latitude, double longitude) {
        String cords = latitude + ", " + longitude;
        mCurrentLocationCordsTextView.setText(cords);
    }

    @Override
    public void updateCurrentLocationAddress(String address) {
        mCurrentLocationAddressTextView.setText(address);
    }

    @Override
    public void startStatusResolution(Status status, int checkSettingsRequestCode) {
        try {
            status.startResolutionForResult(this, checkSettingsRequestCode);
        } catch (IntentSender.SendIntentException e) {

        }
    }

    @Override
    public void showLocationSettingsWarning() {
        Toast.makeText(this, "w", Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayGPSFixFound() {
        //// TODO: 16.02.2016 its temporary
        Toast.makeText(this, "lokalizacja ustalona", Toast.LENGTH_LONG).show();
    }
}
