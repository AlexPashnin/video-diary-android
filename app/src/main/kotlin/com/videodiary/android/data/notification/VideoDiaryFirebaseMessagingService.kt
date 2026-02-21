package com.videodiary.android.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.videodiary.android.MainActivity
import com.videodiary.android.R
import com.videodiary.android.data.local.datastore.TokenDataStore
import com.videodiary.android.data.remote.api.NotificationApi
import com.videodiary.android.data.remote.dto.notification.RegisterDeviceRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FCM service. Deep-link URIs use the custom scheme `videodiary://` registered
 * in AndroidManifest.xml. MainActivity handles the incoming intent and Compose
 * Navigation resolves the deep link automatically via navDeepLink{} entries.
 */
@AndroidEntryPoint
class VideoDiaryFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var tokenDataStore: TokenDataStore
    @Inject lateinit var notificationApi: NotificationApi

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            tokenDataStore.saveFcmToken(token)
            runCatching {
                notificationApi.registerDevice(RegisterDeviceRequest(fcmToken = token))
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val type = data["type"] ?: return

        val (title, body, deepLinkUri) = when (type) {
            TYPE_VIDEO_READY -> Triple(
                "Video ready",
                "Your video has been processed. Select your 1-second moment now.",
                data["videoId"]?.let { "videodiary://clip_select/$it" },
            )
            TYPE_CLIP_READY -> Triple(
                "Clip saved",
                "Your daily clip has been saved successfully.",
                "videodiary://home",
            )
            TYPE_COMPILATION_READY -> Triple(
                "Compilation ready",
                "Your compilation is ready to watch!",
                data["compilationId"]?.let { "videodiary://player/$it" },
            )
            TYPE_COMPILATION_EXPIRING -> Triple(
                "Compilation expiring soon",
                "A compilation will expire in 24 hours. Download it to keep it.",
                "videodiary://compilation_history",
            )
            else -> return
        }

        showNotification(
            title = title,
            body = body,
            deepLinkUri = deepLinkUri ?: "videodiary://home",
            notificationId = type.hashCode(),
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        deepLinkUri: String,
        notificationId: Int,
    ) {
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri.toUri(), this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "video_diary_notifications"
        const val CHANNEL_NAME = "Video Diary"

        private const val TYPE_VIDEO_READY = "VIDEO_READY"
        private const val TYPE_CLIP_READY = "CLIP_READY"
        private const val TYPE_COMPILATION_READY = "COMPILATION_READY"
        private const val TYPE_COMPILATION_EXPIRING = "COMPILATION_EXPIRING"
    }
}
