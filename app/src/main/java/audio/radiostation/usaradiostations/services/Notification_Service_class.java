package audio.radiostation.usaradiostations.services;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.squareup.picasso.Picasso;

import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.constant.Constant;


/**
 * Created by ST_003 on 12-05-2016.
 */
public class Notification_Service_class extends Service {
    public static String data;

    public static Notification_Service_class service;


    RemoteViews remoteViews;
    NotificationManager notificationmanager;
    NotificationCompat.Builder builder;
    Notification foregroundNote;
    int bigIconId = 1002;
    int requset_code = 874;


    static public Notification_Service_class getInstance() {
        if (service == null) {
            service = new Notification_Service_class();
        }
        return service;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("sdncvhin", "called old");
        bigIconId = R.id.iv_station_img;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent == null ? null : intent.getAction();
        Log.d("APP", "service action:" + action);
        if (Constant.ACTION_ENABLE_STICKING.equals(action)) {
            CustomNotification();

        } else if (Constant.ACTION_DISABLE_STICKING.equals(action)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @SuppressLint("ObsoleteSdkInt")
    public void CustomNotification() {
        remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_lay);

        Intent playpauseButton = new Intent(Main.RECIEVER_NOTI_PLAYPAUSE);
        playpauseButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, requset_code, playpauseButton, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingintent = PendingIntent.getActivity(this, requset_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Stop_Not = new Intent(Constant.Stop_Not);
        PendingIntent pendingStop_NotIntent = PendingIntent.getBroadcast(this, requset_code, Stop_Not, 0);


        if (Constant.playpause) {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_pause);
        } else {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_play_arrow);
        }

        Constant.NOTIFICATION_CREATED = true;
        remoteViews.setTextViewText(R.id.tv_title, Constant.station_playing);
        remoteViews.setTextViewText(R.id.tv_radiostationname, Constant.song_name);

        notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("ch_id_i", "name", importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(false);
            mChannel.setShowBadge(false);
            notificationmanager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(getApplicationContext(), "ch_id_i");
        foregroundNote = builder.setSmallIcon(R.drawable.appicon)
                .setTicker(getResources().getString(R.string.app_name))
                // Dismiss Notification
                .setAutoCancel(true)
                .setOngoing(true)
                // Set RemoteViews into Notification
                .setContent(remoteViews)
                .setPriority(Notification.PRIORITY_HIGH)
                .build();

        remoteViews.setTextViewText(R.id.tv_title, Constant.station_playing);
        remoteViews.setTextViewText(R.id.tv_radiostationname, Constant.song_name);

        remoteViews.setOnClickPendingIntent(R.id.iv_station_img, pendingintent);
        remoteViews.setOnClickPendingIntent(R.id.iv_playpause, pendingPlayPauseIntent);
        remoteViews.setOnClickPendingIntent(R.id.close, pendingStop_NotIntent);


        if (Constant.playpause) {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_pause);
        } else {
            remoteViews.setImageViewResource(R.id.iv_playpause, R.drawable.ic_play_arrow);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            foregroundNote.bigContentView = remoteViews;
        }
        startForeground(901, foregroundNote);


        bigIconId = R.id.iv_station_img;

        if (Constant.imageurl.equalsIgnoreCase( "" )) {
            remoteViews.setImageViewResource( R.id.iv_station_img, R.drawable.appicon );
        } else {
            Picasso.with(this).load( Constant.imageurl ).into( remoteViews, bigIconId, 901, foregroundNote );
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
