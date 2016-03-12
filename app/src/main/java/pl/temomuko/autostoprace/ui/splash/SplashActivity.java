package pl.temomuko.autostoprace.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by Szymon Kozak on 2016-03-11.
 */
public class SplashActivity extends BaseActivity {

    @Bind(R.id.iv_sponsor_logo) ImageView mSponsorLogoImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Picasso.with(this)
                .load(R.drawable.logo_linuxpl)
                .placeholder(R.drawable.logo_linuxpl)
                .into(mSponsorLogoImageView);
        new Handler().postDelayed(this::startMainActivity, Constants.SPLASH_DURATION);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
