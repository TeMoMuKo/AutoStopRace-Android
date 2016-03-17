package pl.temomuko.autostoprace.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-03-11.
 */
public class SplashActivity extends BaseActivity {

    private int[] mSponsorDrawablesIds = {
            R.drawable.logo_linuxpl,
            R.drawable.logo_cafe_borowka,
            R.drawable.logo_sueno,
            R.drawable.logo_sygnet,
            R.drawable.logo_tarczynski,
            R.drawable.logo_ttwarsaw,
            R.drawable.logo_unicar_wroclaw,
            R.drawable.logo_xiaoyi,
            R.drawable.logo_zona
    };
    @Bind(R.id.iv_sponsor_logo) ImageView mSponsorLogoImageView;
    @Bind(R.id.iv_splash_logo) ImageView mAppLogoImageView;
    @Bind(R.id.iv_splash_bg) ImageView mBackgroundImageView;
    private Subscription mSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadSponsorLogo();
        loadAppLogo();
        loadBackground();
    }

    private void loadAppLogo() {
        Picasso.with(this)
                .load(R.drawable.logo)
                .placeholder(R.drawable.logo)
                .into(mAppLogoImageView);
    }

    private void loadBackground() {
        Picasso.with(this)
                .load(R.drawable.bg)
                .placeholder(R.drawable.bg)
                .into(mBackgroundImageView);
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
        if(mSubscription != null) mSubscription.unsubscribe();
    }

    private void loadSponsorLogo() {
        int randomDrawableId = getRandomSponsorLogoDrawableId();
        Picasso.with(this)
                .load(randomDrawableId)
                .placeholder(randomDrawableId)
                .into(mSponsorLogoImageView);
    }

    private int getRandomSponsorLogoDrawableId() {
        int randomDrawableIndex = new Random().nextInt(mSponsorDrawablesIds.length);
        return mSponsorDrawablesIds[randomDrawableIndex];
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
