package audio.radiostation.usaradiostations;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.AudioFocusType;
import com.huawei.hms.ads.splash.SplashView;

public class Splash extends AppCompatActivity {

    private int SLEEP_TIMER = 2;
    private static final String AD_ID = "testq6zq98hecj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        loadAd();
        // ADS
        SplashView splashView = findViewById(R.id.splash_ad_view);
        LogoLauncher LogoLauncher = new LogoLauncher();
        LogoLauncher.start();

    }

    private void loadAd() {
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        AdParam adParam = new AdParam.Builder().build();
        SplashView.SplashAdLoadListener splashAdLoadListener = new SplashView.SplashAdLoadListener() {
            @Override
            public void onAdLoaded() {
                // Called when an ad is loaded successfully.
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Called when an ad failed to be loaded. The app home screen is then displayed.
                //      jump();
            }
            @Override
            public void onAdDismissed() {
                // Called when the display of an ad is complete. The app home screen is then displayed.
                //    jump();
            }
        };
        // Obtain SplashView.
        SplashView splashView = findViewById(R.id.splash_ad_view);
        // Set the audio focus preemption policy for a video splash ad.
        splashView.setAudioFocusType(AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE);
        // Load the ad. AD_ID indicates the ad slot ID.
        splashView.load(AD_ID, orientation, adParam, splashAdLoadListener);

    }

    private class LogoLauncher extends Thread {
        public void run() {
            try {
                sleep(1000 * SLEEP_TIMER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Splash.this, Main.class);
            startActivity(intent);
            Splash.this.finish();

        }

    }
}