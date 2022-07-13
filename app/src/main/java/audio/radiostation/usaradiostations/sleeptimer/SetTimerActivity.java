package audio.radiostation.usaradiostations.sleeptimer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;

import audio.radiostation.usaradiostations.R;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SetTimerActivity extends AppCompatActivity {

    private static final String LOG_TAG = SetTimerActivity.class.getName();

    private static final String HOURS_KEY = MainActivity.class.getName() + ".hours";
    private static final String MINUTES_KEY = MainActivity.class.getName() + ".minutes";
    long current_Time, set_time;
    TimePicker tp;
    Toolbar toolbar;
    private TimerManager timerManager;
    private SharedPreferences sharedPreferences;
    private CountdownNotifier countdownNotifier;
    private PauseMusicNotifier pauseMusicNotifier;



    ImageView back_press;

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("setNumberPicColor", e);
                } catch (IllegalAccessException e) {
                    Log.w("setNumberPColor", e);
                } catch (IllegalArgumentException e) {
                    Log.w("setNumbTextColor", e);
                }
            }
        }
        return false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        onCreate(
                savedInstanceState,
                TimerManager.get(this),
                PreferenceManager.getDefaultSharedPreferences(this),
                CountdownNotifier.get(this),
                PauseMusicNotifier.get(this));
    }

    protected void onCreate(
            Bundle savedInstanceState,
            TimerManager timerManager,
            SharedPreferences sharedPreferences,
            CountdownNotifier countdownNotifier,
            PauseMusicNotifier pauseMusicNotifier) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time2r);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.sleep_timer);
        toolbar.setTitleTextColor(0xffffffff);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        tp = (TimePicker) findViewById(R.id.timepicker);
        HwAds.init(this);

        BannerView bannerView=findViewById(R.id.hw_banner_view);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);
        // hoursPicker = (NumberPicker) findViewById(R.id.hours_picker);
//        setNumberPickerTextColor(hoursPicker, Color.BLACK);
        //  hoursPicker.setMinValue(MIN_HOURS);
        //  hoursPicker.setMaxValue(MAX_HOURS);
        //  hoursPicker.setValue(sharedPreferences.getInt(HOURS_KEY, DEFAULT_HOURS));

        // minutesPicker = (NumberPicker) findViewById(R.id.minutes_picker);
        //   setNumberPickerTextColor(minutesPicker, Color.BLACK);
        //  minutesPicker.setMinValue(MIN_MINUTES);
        //    minutesPicker.setMaxValue(MAX_MINUTES);
        //    minutesPicker.setValue(sharedPreferences.getInt(MINUTES_KEY, DEFAULT_MINUTES));

        this.timerManager = timerManager;
        this.sharedPreferences = sharedPreferences;
        this.countdownNotifier = countdownNotifier;
        this.pauseMusicNotifier = pauseMusicNotifier;

        getDateTime();
    }

    private void getDateTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm");
        String localTime = date.format(currentLocalTime);
        current_Time = getDateInMillis(localTime);
        Log.e("TimeCu", "" + localTime);
        Log.e("TimeCu", "" + current_Time);
    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("HH:mm");
        desiredFormat.setTimeZone(TimeZone.getDefault());
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            //  Log.e("DATE IN MILLIS", String.valueOf(dateInMillis));
            return dateInMillis;
        } catch (ParseException e) {
            // Log.d("Exception date. " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public void startTimer(View view) {

        int hourOfDay = tp.getCurrentHour();
        int minute = tp.getCurrentMinute();
        String timee = hourOfDay + ":" + minute;
        set_time = getDateInMillis(timee);

        long f_time = set_time - current_Time;

        int hhrr = (int) TimeUnit.MILLISECONDS.toHours(f_time);
        int mitu = (int) TimeUnit.MILLISECONDS.toMinutes(f_time);


        Log.e("TimeC", "" + hhrr);
        Log.e("TimeC", "" + set_time);
        Log.e("Time", "" + mitu);

        String AMPM = "AM";
        if (hourOfDay > 11) {

            hourOfDay = hourOfDay - 12;
            AMPM = "PM";
        }

        int hours = hourOfDay;
        int minutes = minute;

        setDefaultTimerLength(hhrr, mitu);

        pauseMusicNotifier.cancelNotification();

        timerManager.setTimer(hhrr, mitu);

        countdownNotifier.postNotification(timerManager.getScheduledTime());

        Toast.makeText(this, R.string.timer_started, Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    private void setDefaultTimerLength(int hours, int minutes) {
        sharedPreferences.edit()
                .putInt(HOURS_KEY, hours)
                .putInt(MINUTES_KEY, minutes)
                .apply();
    }


}
