package com.videodiary.android.data.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// TODO Phase 9: Register FCM token with backend and handle deep links
class VideoDiaryFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to backend via POST /notifications/register-device
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: Handle notification types (video ready, clip ready, compilation ready, expiring)
    }
}
