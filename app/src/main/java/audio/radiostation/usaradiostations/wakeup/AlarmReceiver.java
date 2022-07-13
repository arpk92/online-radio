package audio.radiostation.usaradiostations.wakeup;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;

import java.util.Calendar;

public final class AlarmReceiver extends BroadcastReceiver {

    private static final String ALARM_EXTRA = "gjytju";

    @Override
    public void onReceive(Context context, Intent intent) {



        final Alarm alarm = intent.getParcelableExtra(ALARM_EXTRA);
        final String value = intent.getExtras().getString("alaram_values");
        String stt = intent.getStringExtra("alaram_values");

        Log.d("VALUE_ALARM","value> "+value);
        Log.d("VALUE_ALARM","alarm> "+alarm);
        localPushNotifications26GreaterAPI(context,value);
        setReminderAlarm(context, alarm);
    }

    public void localPushNotifications26GreaterAPI(Context context,String alarm) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Log.d("alarm_extra", "oreo recieved");

            // The id of the channel.
            String id = "my_channel_01";
            CharSequence name = "iam.peace";
            String description = "Description";

            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            Notification notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Radio")
                    .setSmallIcon(R.drawable.appicon)
                    .setChannelId(CHANNEL_ID)
                    .build();
            mNotificationManager.notify(notifyID, notification);
            context.startActivity(new Intent(context, Main.class)
                    .putExtra("isopening","yes")
                    .putExtra("myvalues",alarm)
                    .putExtra("fromoreo","yes")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } else {
            context.startActivity(new Intent(context, Main.class)
                    .putExtra("isopening","yes")
                    .putExtra("myvalues",alarm)
                    .putExtra("fromoreo","no")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }


    }

    public static void setReminderAlarm(Context context, Alarm alarm) {
        Log.d("setalarm_activity", "alarm>>"+alarm);

        try {
            if (!AlarmUtils.isAlarmActive(alarm)) {
                //If alarm not set to run on any days, cancel any existing notifications for this alarm
                cancelReminderAlarm(context, alarm);
                return;
            }

            final Calendar nextAlarmTime = getTimeForNextAlarm(alarm);
            alarm.setTime(nextAlarmTime.getTimeInMillis());

            final Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(ALARM_EXTRA, alarm);
            intent.putExtra("alaram_values", alarm.getLabel());
            Log.d("VALUE_ALARM",""+alarm.getLabel());

            final PendingIntent pIntent = PendingIntent.getBroadcast(
                    context,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            am.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), pIntent);
        } catch (Exception e) {
            Log.d("setalarm_activity", "error >>"+e);

        }


    }

    /**
     * Calculates the actual time of the next alarm/notification based on the user-set time the
     * alarm should sound each day, the days the alarm is set to run, and the current time.
     *
     * @param alarm Alarm containing the daily time the alarm is set to run and days the alarm
     *              should run
     * @return A Calendar with the actual time of the next alarm.
     */
    private static Calendar getTimeForNextAlarm(Alarm alarm) {

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarm.getTime());

        final long currentTime = System.currentTimeMillis();
        final int startIndex = getStartIndexFromTime(calendar);

        int count = 0;
        boolean isAlarmSetForDay;

        final SparseBooleanArray daysArray = alarm.getDays();

        do {
            final int index = (startIndex + count) % 7;
            isAlarmSetForDay =
                    daysArray.valueAt(index) && (calendar.getTimeInMillis() > currentTime);
            if (!isAlarmSetForDay) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                count++;
            }
        } while (!isAlarmSetForDay && count < 7);

        return calendar;

    }

    public static void cancelReminderAlarm(Context context, Alarm alarm) {

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                AlarmUtils.getNotificationId(alarm),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pIntent);
    }

    private static int getStartIndexFromTime(Calendar c) {

        final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int startIndex = 0;
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                startIndex = 0;
                break;
            case Calendar.TUESDAY:
                startIndex = 1;
                break;
            case Calendar.WEDNESDAY:
                startIndex = 2;
                break;
            case Calendar.THURSDAY:
                startIndex = 3;
                break;
            case Calendar.FRIDAY:
                startIndex = 4;
                break;
            case Calendar.SATURDAY:
                startIndex = 5;
                break;
            case Calendar.SUNDAY:
                startIndex = 6;
                break;
        }

        return startIndex;

    }

}
