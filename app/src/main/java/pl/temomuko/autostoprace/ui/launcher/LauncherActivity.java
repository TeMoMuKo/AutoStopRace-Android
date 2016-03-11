package pl.temomuko.autostoprace.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setupToolbarWithToggle();
        setListeners();
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
