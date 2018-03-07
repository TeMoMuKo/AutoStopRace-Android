package pl.temomuko.autostoprace;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.leakcanary.LeakCanary;

import java.util.Locale;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import pl.temomuko.autostoprace.injection.component.ApplicationComponent;
import pl.temomuko.autostoprace.injection.component.DaggerApplicationComponent;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;
import pl.temomuko.autostoprace.service.LocationSyncService;
import pl.temomuko.autostoprace.service.receiver.NetworkChangeReceiver;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
public class AsrApplication extends Application {

    @Inject LocationSyncService.NetworkChangeReceiver mServiceNetworkReceiver;
    @Inject NetworkChangeReceiver mNetworkReceiver;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        getComponent().inject(this);
        Fabric.with(this, new Crashlytics());
        Locale.setDefault(new Locale(Constants.DEFAULT_LOCALE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mServiceNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    public static AsrApplication get(Context context) {
        return (AsrApplication) context.getApplicationContext();
    }
}
