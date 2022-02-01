package com.wisekrakr.wisemessenger.firebase


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wisekrakr.wisemessenger.R
import com.wisekrakr.wisemessenger.api.model.Notification
import com.wisekrakr.wisemessenger.api.model.nondata.NotificationType
import com.wisekrakr.wisemessenger.components.activity.HomeActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {

            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body,
            null
            )
        }
    }

    fun onNotificationReceived(notification: Notification, context: Context) {
        when (notification.type) {
            NotificationType.CHAT_REQUEST -> {
                showNotification(
                    "Chat Request",
                    notification.fromUserName + " wants to chat with you",
                    context
                )
            }
            NotificationType.MESSAGE -> {
                showNotification(
                    "Chat Message",
                    "You got a new message from " + notification.fromUserName,
                    context
                )
            }
            else->{}
        }

    }

    private fun getCustomDesign(
        title: String?,
        message: String?,
        context: Context?
    ): RemoteViews {

        val remoteViews = RemoteViews(
            context!!.applicationContext.packageName,
            R.layout.push_notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.notification_icon,
            R.drawable.logo_small)
        return remoteViews
    }

    private fun showNotification(
        title: String?,
        message: String?,
        context: Context?,
    ) {
        val intent = Intent(context, HomeActivity::class.java)
        val channelId = "notification_channel"

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)// clears all activities and puts Home on top
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context!!.applicationContext,
            channelId)
            .setSmallIcon(R.drawable.logo_small)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000,
                1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getCustomDesign(title, message,context))

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                channelId, context.applicationContext.packageName,
                NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    lightColor = Color.RED
                    enableLights(true)
                }

            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }
}