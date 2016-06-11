package pl.temomuko.autostoprace.ui.staticdata.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.staticdata.PartnersDrawables;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-03-11.
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_sponsor_logo) ImageView mSponsorLogoImageView;
    @BindView(R.id.iv_splash_logo) ImageView mAppLogoImageView;
    @BindView(R.id.iv_splash_bg) ImageView mBackgroundImageView;

    private Subscription mSubscription;
    private Integer[] mPartnersDrawables;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupPartnersList();
        loadBackground();
        loadAppLogo();
        loadSponsorLogo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubscription = Observable.timer(Constants.SPLASH_DURATION, TimeUnit.MILLISECONDS)
                .compose(RxUtil.applyIoSchedulers())
                .doOnCompleted(this::startMainActivity)
                .subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    private void setupPartnersList() {
        mPartnersDrawables = PartnersDrawables.getAsArray();
    }

    private void loadBackground() {
        Picasso.with(this)
                .load(R.drawable.bg)
                .placeholder(R.drawable.bg)
                .into(mBackgroundImageView);
    }

    private void loadAppLogo() {
        Picasso.with(this)
                .load(R.drawable.logo_asr)
                .placeholder(R.drawable.logo_asr)
                .into(mAppLogoImageView);
    }

    private void loadSponsorLogo() {
        int randomDrawableId = getRandomPartnerLogoDrawableId();
        mSponsorLogoImageView.setImageResource(randomDrawableId);
    }

    private int getRandomPartnerLogoDrawableId() {
        int randomDrawableIndex = new Random().nextInt(mPartnersDrawables.length);
        return mPartnersDrawables[randomDrawableIndex];
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
