package pl.temomuko.autostoprace.service

import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.temomuko.autostoprace.AsrApplication
import pl.temomuko.autostoprace.R
import pl.temomuko.autostoprace.ui.main.MainActivity
import pl.temomuko.autostoprace.util.getColorCompat

class AsrFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, messageBody: String?) {
        val contentTitle = title ?: getString(R.string.app_name)

        val notification = NotificationCompat.Builder(this, AsrApplication.Channels.GENERAL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(contentTitle)
            .setContentText(messageBody)
            .setColor(getColorCompat(R.color.accent))
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, -1,
                    createDefaultLauncherIntent(), PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun createDefaultLauncherIntent() = Intent(this, MainActivity::class.java).apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    companion object {
        private const val NOTIFICATION_ID = -1
    }
}
