package pl.temomuko.autostoprace.ui.main;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.ApiErrorResponse;
import pl.temomuko.autostoprace.data.model.Location;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.util.ApiResponseUtil;

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
        mMainPresenter.loadLocationsFromApi();
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
    public void showError(Throwable throwable) {
        ApiErrorResponse response = ApiResponseUtil.getErrorResponse(this, throwable);
        Log.e(TAG, response.getErrorMessage());
    }
}
