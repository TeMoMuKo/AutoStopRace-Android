package pl.temomuko.autostoprace.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class LauncherActivity extends DrawerActivity {

    @Bind(R.id.btn_go_to_login) Button mGoToLoginButton;
    @Bind(R.id.btn_go_to_contact) Button mGoToContactButton;
    @Bind(R.id.iv_launcher_logo) ImageView mAppLogoImageView;
    @Bind(R.id.iv_launcher_bg) ImageView mBackgroundImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setupToolbarWithToggle();
        loadAppLogo();
        loadBackground();
        setListeners();
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

    private void setListeners() {
        mGoToLoginButton.setOnClickListener(v -> startLoginActivity());
        mGoToContactButton.setOnClickListener(v -> startContactActivity());
    }

    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void startContactActivity() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
}
