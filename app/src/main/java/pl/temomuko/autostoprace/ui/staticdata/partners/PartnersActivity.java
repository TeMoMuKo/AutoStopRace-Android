package pl.temomuko.autostoprace.ui.staticdata.partners;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import javax.inject.Inject;

import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.temomuko.autostoprace.BuildConfig;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;
import pl.temomuko.autostoprace.ui.staticdata.StaticDrawerPresenter;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */

public class PartnersActivity extends DrawerActivity implements DrawerMvpView {

    public static final String ASR_ORIGIN = "autostoprace.pl";

    @Inject StaticDrawerPresenter mPresenter;

    @BindView(R.id.partnersWebView) WebView partnersWebView;
    @BindView(R.id.loadingProgressBar) MaterialProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partners);
        getActivityComponent().inject(this);
        mPresenter.attachView(this);
        mPresenter.setupUserInfoInDrawer();
        setupPartnersWebView();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    private void setupPartnersWebView() {
        partnersWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                hideLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains(ASR_ORIGIN)) {
                    view.loadUrl(url);
                } else {
                    Intent webPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(webPageIntent);
                }
                return true;
            }
        });
        partnersWebView.loadUrl(BuildConfig.APP_BASE_URL + "/raceinfo/partners");
    }

    private void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }
}
