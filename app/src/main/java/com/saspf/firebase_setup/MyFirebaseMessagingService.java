package com.saspf.firebase_setup;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        getFirebaseMessage(message.getNotification().getTitle(), message.getNotification().getBody());
    }

    @SuppressLint("MissingPermission")
    private void getFirebaseMessage(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
                .setSmallIcon(R.drawable.notifications_active)
                .setContentText(title)
                .setContentText(body)
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(102, builder.build());
    }
}
