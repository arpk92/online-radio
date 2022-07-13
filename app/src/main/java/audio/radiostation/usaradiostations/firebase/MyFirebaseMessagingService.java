package audio.radiostation.usaradiostations.firebase;

/**
 * Created by ST001 on 07-03-2018.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d( TAG, "From: " + remoteMessage.getFrom() );
        if (remoteMessage.getData().size() > 0) {
            Log.d( TAG, "Message data payload: " + remoteMessage.getData() );

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }


        if (remoteMessage.getNotification() != null) {
            Log.d( TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() );

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
        }
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        sendNotification( body );
    }


    private void handleNow() {
        Log.d( TAG, "Short lived task is done." );
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent( this, Main.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString( R.string.app_name );
        Uri defaultSoundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder( this, channelId )
                        .setSmallIcon( R.drawable.appicon )
                        .setContentTitle( channelId )
                        .setContentText( messageBody )
                        .setColor( getResources().getColor( R.color.colorPrimary ) )
                        .setAutoCancel( true )
                        .setSound( defaultSoundUri )
                        .setContentIntent( pendingIntent );

        NotificationManager notificationManager =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel( channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT );
//            notificationManager.createNotificationChannel( channel );
//        }
        notificationManager.notify( 0, notificationBuilder.build() );

/*
        Intent resultIntent = new Intent(this, Main.class);
        // resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);


      *//*  Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 // Request code //, intent,
                PendingIntent.FLAG_ONE_SHOT);*//*

        String channelId = getString(R.string.app_name);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(messageBody)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(resultPendingIntent);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());*/
    }


}