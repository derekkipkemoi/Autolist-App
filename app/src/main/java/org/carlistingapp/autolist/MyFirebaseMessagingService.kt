package org.carlistingapp.autolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.carlistingapp.autolist.data.network.ListingCarsAPI
import org.carlistingapp.autolist.ui.home.HomeActivity
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModel
import org.carlistingapp.autolist.ui.home.profile.viewModel.UserViewModelFactory
import org.carlistingapp.autolist.utils.Session
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MyFirebaseMessagingService : FirebaseMessagingService(), KodeinAware {
    override val kodein by kodein()
    val session : Session by instance()
    private val api : ListingCarsAPI by instance()
    private val factory : UserViewModelFactory by instance()
    private lateinit var viewModel : UserViewModel
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        session.saveUserFcmToken(token)
//        if (session.getSession().toString() != "userLoggedOut"){
//            sendRegistrationToServer(token, session.getSession().toString())
//        }
    }

    private fun sendRegistrationToServer(token: String?, userId: String) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }



    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_24)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}