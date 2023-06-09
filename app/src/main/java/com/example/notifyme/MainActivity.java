package com.example.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button button_notify;
    private static final String PRIMARY_CHANNEL_ID = "primary_notificaion_channel";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 0;
    private  Button button_cancel, button_update;
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";

    private NotificationReceiver mReceiver = new NotificationReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    button_notify = findViewById(R.id.notify);
    button_notify.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNotification();
        }
    });
    createNotificationChannel();

    button_update = findViewById(R.id.update);
    button_update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateNotification();
        }
    });

    button_cancel = findViewById(R.id.cancel);
    button_cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelNotification();
        }
    });

    registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

    setNotificationButtonState(true, false, false);

    }
    public void updateNotification(){
        Bitmap androidImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);

    }
    public void cancelNotification(){
        mNotifyManager.cancel(NOTIFICATION_ID);
        Toast.makeText(this, "Notification Cancelled!", Toast.LENGTH_SHORT).show();
        setNotificationButtonState(true, false, false);
    }


    private NotificationCompat.Builder getNotificationBuilder(){

        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent notifPendIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notifIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                        .setContentTitle("You've veen notified!")
                        .setContentText("This is your notification text")
                        .setSmallIcon(R.drawable.ic_android)
                        .setContentIntent(notifPendIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void sendNotification(){
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        getNotificationBuilder();
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        Toast.makeText(this, "Notification Displayed!", Toast.LENGTH_SHORT).show();


        setNotificationButtonState(false, true, true);

    }

    public void createNotificationChannel(){
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);

        }
    }
    void setNotificationButtonState(Boolean isNotifyEnabled,
                                    Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }
    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}