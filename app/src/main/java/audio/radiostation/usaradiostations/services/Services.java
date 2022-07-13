package audio.radiostation.usaradiostations.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import audio.radiostation.usaradiostations.constant.Constant;
import java.util.Objects;


@SuppressLint("Registered")
public class Services extends Service implements ExoPlayer.EventListener {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String station;
    private static Services service;
    public static SimpleExoPlayer exoPlayer;
    private static Uri uri;
    private static WifiManager.WifiLock wifiLock;
    static ProgressTask task;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent == null ? null : intent.getAction();
        Log.d("APP", "service action:" + action);
        if (Constant.ACTION_ENABLE_STICKING.equals(action)) {
            task = new ProgressTask();
            task.execute();

        } else if (Constant.ACTION_DISABLE_STICKING.equals(action)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    static public void initialize(Context context, String station) {
      Services.context = context;
       Services.station = station;
        Log.e("inwhich", "");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance( new DefaultRenderersFactory(this), trackSelector, loadControl);

          /*  exoPlayer =   ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(), new DefaultLoadControl());*/

        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText( Services.this, "Unable to play This Station", Toast.LENGTH_SHORT ).show();
        }

    }

    public static void pause() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);

        }
    }
    public static void pausefm() {
        if (exoPlayer != null) {
            exoPlayer.stop();

        }
    }

    public static void start()    {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            Log.e("Destroyed", "Called");
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        Log.d("myprogress","onTimelineChanged");

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("myprogress","onTracksChanged");

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("myprogress","onLoadingChanged");

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        Log.d("myprogress",""+playbackState);


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.d("myprogress","onRepeatModeChanged");

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        Log.d("myprogress","onShuffleModeEnabledChanged");

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d("myprogress","onPlayerError");

    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.d("myprogress","onPositionDiscontinuity");

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d("myprogress","onPlaybackParametersChanged");

    }

    @Override
    public void onSeekProcessed() {
        Log.d("myprogress","onSeekProcessed");

    }


    @SuppressLint("StaticFieldLeak")
    public class ProgressTask extends AsyncTask<String, Void, Boolean> {


        protected void onPreExecute() {
        }

        protected Boolean doInBackground(final String... args) {

            try {
                uri = Uri.parse(station);

                DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context,"ituneon Radio"), bandwidthMeterA);

                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//                MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
//                MediaSource videoSource = new HlsMediaSource(uri, dataSourceFactory, 1, null, null);


                MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .setExtractorsFactory(new DefaultExtractorsFactory())
                        .createMediaSource(uri);


                MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

             /*   DashMediaSource.Factory(dataSourceFactory, dataSourceFactory)
                        .setManifestParser(new CustomManifestParser())
                        .createMediaSource(uri, null, null)*/
                final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

                if (station.endsWith(".m3u8")) {
                    exoPlayer.prepare(loopingSource);
                } else {
                    exoPlayer.prepare(audioSource);
                }

            } catch (IllegalArgumentException | IllegalStateException | SecurityException | NullPointerException e1) {
                e1.printStackTrace();

            }
            return true;
        }

        @SuppressLint({"WifiManagerPotentialLeak", "WakelockTimeout"})
        @Override
        protected void onPostExecute(final Boolean success) {
            try
            {
                if (success)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    {
                        wifiLock = ((WifiManager) Objects.requireNonNull( context.getSystemService( Context.WIFI_SERVICE ) ))
                                .createWifiLock( WifiManager.WIFI_MODE_FULL, "RadiophonyLock");
                    }
                    wifiLock.acquire();
                    PowerManager pm = (PowerManager) getSystemService( Context.POWER_SERVICE);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
                    wl.acquire();
                    exoPlayer.setPlayWhenReady(true);
                }
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
                Toast.makeText( Services.this, "Unable to play This Station", Toast.LENGTH_SHORT ).show();
            }
        }
    }
}