package com.example.chrisantuseze.palcechat.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.chrisantuseze.palcechat.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by CHRISANTUS EZE on 28/11/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationMessage = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        Intent resultIntent = new Intent(click_action);

        resultIntent.putExtra("user_key", from_user_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        int mNotificationId = (int)System.currentTimeMillis();

        NotificationManager mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
