package pl.temomuko.autostoprace.ui.main;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by szymen on 2016-01-06.
 */
public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject MainPresenter mMainPresenter;
    private String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);
        mMainPresenter.attachView(this);
        mMainPresenter.loadLocations();
    }

    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void updateLocationsList(List<Location> locations) {
        Log.i(TAG, locations.toString());
    }

    @Override
    public void showApiError(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void showEmptyInfo() {
        Log.e(TAG, getString(R.string.msg_empty_locations_list));
    }
}
