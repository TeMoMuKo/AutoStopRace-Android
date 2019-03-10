package pl.temomuko.autostoprace

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import com.crashlytics.android.Crashlytics
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import pl.temomuko.autostoprace.injection.component.ApplicationComponent
import pl.temomuko.autostoprace.injection.component.DaggerApplicationComponent
import pl.temomuko.autostoprace.injection.module.ApplicationModule
import pl.temomuko.autostoprace.service.LocationSyncService
import pl.temomuko.autostoprace.service.receiver.NetworkChangeReceiver
import java.util.*
import javax.inject.Inject

class AsrApplication : Application() {

    @Inject lateinit var mServiceNetworkReceiver: LocationSyncService.NetworkChangeReceiver
    @Inject lateinit var mNetworkReceiver: NetworkChangeReceiver

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        initFlavorConfig(this)
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) registerNotificationChannels()
        applicationComponent.inject(this)
        Fabric.with(this, Crashlytics())
        Locale.setDefault(Locale(Constants.DEFAULT_LOCALE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mServiceNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerNotificationChannels() {
        val generalChannel = NotificationChannel(
            Channels.GENERAL,
            getString(R.string.general_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(generalChannel)
    }

    companion object {
        @JvmStatic
        fun getApplicationComponent(context: Context): ApplicationComponent {
            return (context.applicationContext as AsrApplication).applicationComponent
        }
    }

    object Channels {
        const val GENERAL = "general"
    }
}
