package pl.temomuko.autostoprace.ui.staticdata.about;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class LicensesActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.wv_licenses) WebView mLicensesWebView;
    @BindView(R.id.mpb_licenses_loading_progress) MaterialProgressBar mLoadingProgressCircle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        setupToolbarWithBack();
        loadLicenses();
    }

    private void setupToolbarWithBack() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadLicenses() {
        mLicensesWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                hideLoading();
            }
        });
        mLicensesWebView.loadUrl(Constants.LICENSES_ASSET_URI);
    }

    private void hideLoading() {
        mLoadingProgressCircle.setVisibility(View.GONE);
    }
}
