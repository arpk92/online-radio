package audio.radiostation.usaradiostations;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.vodyasov.amr.AudiostreamMetadataManager;
import com.vodyasov.amr.OnNewMetadataListener;
import com.vodyasov.amr.UserAgent;
import de.hdodenhof.circleimageview.CircleImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import audio.radiostation.usaradiostations.Utilities.Blur;
import audio.radiostation.usaradiostations.Utilities.CircularSeekBar;
import audio.radiostation.usaradiostations.adapters.Player_adapter;
import audio.radiostation.usaradiostations.clases.Favorite_save_Prefrence;
import audio.radiostation.usaradiostations.clases.Recent_list;
import audio.radiostation.usaradiostations.constant.Constant;
import audio.radiostation.usaradiostations.constant.HttpHandler;
import audio.radiostation.usaradiostations.fragments.*;
import audio.radiostation.usaradiostations.model_class.Model_class;
import audio.radiostation.usaradiostations.services.Notification_Service_class;
import audio.radiostation.usaradiostations.services.Services;
import audio.radiostation.usaradiostations.wakeup.MainActivityalarm;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends AppCompatActivity {
    private static final String ns = null;
    public static String RECIEVER_NOTI_PLAYPAUSE = "play_play_reciever";
    ArrayList<Model_class> mylist = new ArrayList();
    public SlidingUpPanelLayout mLayout;
    RelativeLayout mini_player;
    RelativeLayout conatiner, timingas;
    public static TextView textstart, textend;
    InputStream inputStream;
    FileOutputStream fileOutputStream;

    TabLayout tabLayout;
    LinearLayout home_dr, fav_dr, recent_dr, record_dr, exit_dr,privacy_policy;

    private static String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.INTERNET",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    public void onBackPressed() {
        if (drawer_lay.isDrawerOpen(Gravity.START)) {
            drawer_lay.closeDrawer(Gravity.START);
        } else {
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                if (Constant.fragopen.equalsIgnoreCase("home")) {
                    Showdialoge();

                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    TextView radio_name, radio_name2, song_name, song_name2;
    ImageView back_image, close_panal, menu_player, mini_image, play_pause_mini, alarm, sleep_timer, play_pause_main,
            iv_share, record, iv_favourite, search, drawer_menu, exit;
    ImageButton previus_play, nextplay;
    CircleImageView circular_image;
    CircularSeekBar circularSeekBar;
    private AudioManager audioManager = null;
    PlayPause reciever_playpause;
    RelativeLayout dragView, stop_recor, snak;
    RecyclerView recyclerview_stations;
    LinearLayout player_icon_lay;
    DrawerLayout drawer_lay;


    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d("wdvhs", "CALL_STATE_RINGING");

                try {
                    if (Services.exoPlayer != null) {
                        if (Constant.playpause == true) {
                            Services.pausefm();
//                            playpausem.setImageResource(R.drawable.play);
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                Log.d("wdvhs", "CALL_STATE_IDLE");

                try {
                    if (Services.exoPlayer != null) {
                        if (Constant.playpause == true) {
                            Services.initialize(Main.this, Constant.streamurl);
                            Constant.playpause = true;
                            Intent kkk = new Intent(Main.this, Services.class);
                            kkk.setAction(Constant.ACTION_ENABLE_STICKING);
                            startService(kkk);
                            Intent startIntent1 = new Intent(Main.this, Notification_Service_class.class);
                            startIntent1.setAction(Constant.ACTION_ENABLE_STICKING);
                            startService(startIntent1);
//                            playpausem.setImageResource(R.drawable.pause);
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.d("wdvhs", "CALL_STATE_OFFHOOK");

                try {
                    if (Services.exoPlayer != null) {
                        if (Constant.playpause == true) {
                            Services.pausefm();
//                            playpausem.setImageResource(R.drawable.play);
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    };


    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {

                    Log.d("wdvhs", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK" + focusChange);

                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            // Lower the volume while ducking.
                            Log.d("wdvhs", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");

                            if (Services.exoPlayer != null) {
                                Services.exoPlayer.setVolume(0.2f);
                            }
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            Log.d("wdvhs", "AUDIOFOCUS_LOSS_TRANSIENT");

                            if (Services.exoPlayer != null) {
                                if (Constant.playpause) {

                                    Services.pausefm();
                                    play_pause_main.setImageResource(R.drawable.ic_play_arrow);
                                    play_pause_mini.setImageResource(R.drawable.ic_play_arrow);
                                    Intent startIntent1 = new Intent(Main.this, Notification_Service_class.class);
                                    startIntent1.setAction(Constant.ACTION_ENABLE_STICKING);
                                    startService(startIntent1);
                                }
                            }
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS):
                            Log.d("wdvhs", "AUDIOFOCUS_LOSS");

                            if (Services.exoPlayer != null) {
                                if (Constant.playpause) {

                                    Services.pausefm();
                                    play_pause_main.setImageResource(R.drawable.ic_play_arrow);
                                    play_pause_mini.setImageResource(R.drawable.ic_play_arrow);
                                    Intent startIntent1 = new Intent(Main.this, Notification_Service_class.class);
                                    startIntent1.setAction(Constant.ACTION_ENABLE_STICKING);
                                    startService(startIntent1);
                                }
                            }
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            Log.d("wdvhs", "AUDIOFOCUS_GAIN");
                            if (Services.exoPlayer != null) {
                                if (Constant.playpause) {
                                    play();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            };


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();

            String message = b.getString("message");

            Log.e("newmesage", "" + message);
            Showdialoge();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ads
        HwAds.init(this);

        BannerView bannerView=findViewById(R.id.hw_banner_view);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);


        assignids();
        setupviewpager();
        new GetXMLFromServer().execute();
        volumeset();
        RegisterReciever();
        Search_fragment home_fragment = new Search_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.conatiner, home_fragment).commit();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.fragopen = "srch";
                search("visible");
            }
        });
        new GetXMLFromServer().execute();


        try {

            String in = getIntent().getExtras().getString("isopening");
            if (in != null) {


                if (in.equalsIgnoreCase("yes")) {


                    String fromoreo = getIntent().getExtras().getString("fromoreo");

                    if (fromoreo != null) {
                        if (fromoreo.equalsIgnoreCase("yes")) {
                            Recent_list sharedPreference = null;

                            List<Model_class> mylist = new ArrayList();
                            mylist.clear();
                            mylist = sharedPreference.getInstance(this).getFavorites(this);
                            Collections.reverse(mylist);
                            if (mylist.size() != 0 && mylist != null) {

                                int i = 0;
                                Constant.station_playing = mylist.get(i).getStation_name();
                                Constant.imageurl = mylist.get(i).getStation_image();
                                Constant.streamurl = mylist.get(i).getStation_url();
                                Constant.position = i;
                                Constant.Model_fav = mylist.get(i);
                                setmaindata();
                                play();
                            }
                        } else {
                            Log.d("myindtent", "value>> " + in);
                            String example = getIntent().getExtras().getString("myvalues");
                            if (example != null) {
                                String URL = example.substring(example.lastIndexOf("|") + 1);
                                String two = example.replace("|" + URL, "");
                                String imaGE = two.substring(example.indexOf("|") + 1);
                                String NAME = two.replace("|" + imaGE, "");
                                Log.d("myremindertick", "URL>>" + URL);
                                Log.d("myremindertick", "imaGE>>" + imaGE);
                                Log.d("myremindertick", "NAME>>" + NAME);
                                Constant.station_playing = NAME;
                                Constant.imageurl = imaGE;
                                Constant.streamurl = URL;

                                Constant.position = 0;
                                setmaindata();
                                play();

                            }
                        }

                    }


                }

            }
        } catch (Exception e) {

        }


        iv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Favorite_save_Prefrence.getInstance(Main.this).isFavorite(Constant.Model_fav)) {
                    Favorite_save_Prefrence.getInstance(Main.this).removeFavorite(Main.this, Constant.Model_fav);
//                    Toast.makeText(getApplicationContext(), "Removed from favorite", Toast.LENGTH_SHORT).show();
                    iv_favourite.setImageResource(R.drawable.ic_favorite_empty);

                    customtoast("Removed from favorite");
                } else {
                    Favorite_save_Prefrence.getInstance(Main.this).addFavorite(Main.this, Constant.Model_fav);
//                    Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    iv_favourite.setImageResource(R.drawable.ic_favorite_fill);
                    customtoast("Added to favorites");
                }
            }
        });
        record.setOnClickListener(new C02211());
        stop_recor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    record.setVisibility(View.VISIBLE);
                    stop_recor.setVisibility(View.INVISIBLE);
                    Constant.isrecording = false;
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                    customtoast("Recording Done");

                } catch (Exception e) {

                }
            }
        });

        close_panal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));


        nextplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    int size = mylist.size() - 1;
                    if (Constant.position >= size) {
                        int i = 0;
                        Constant.station_playing = mylist.get(i).getStation_name();
                        Constant.imageurl = mylist.get(i).getStation_image();
                        Constant.streamurl = mylist.get(i).getStation_url();
                        Constant.position = i;
                        Constant.Model_fav = mylist.get(i);
                        setmaindata();
                        play();
                        addtorecent(mylist.get(i));
                        check_favorite(mylist.get(i));
                    } else {
                        int i = Constant.position + 1;
                        Constant.station_playing = mylist.get(i).getStation_name();
                        Constant.imageurl = mylist.get(i).getStation_image();
                        Constant.streamurl = mylist.get(i).getStation_url();
                        Constant.position = i;
                        Constant.Model_fav = mylist.get(i);
                        setmaindata();
                        play();
                        addtorecent(mylist.get(i));
                        check_favorite(mylist.get(i));
                    }
                } catch (Exception e) {

                }
            }
        });

        previus_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int size = mylist.size() - 1;
                    if (Constant.position == 0) {
                        int i = size;
                        Constant.station_playing = mylist.get(i).getStation_name();
                        Constant.imageurl = mylist.get(i).getStation_image();
                        Constant.streamurl = mylist.get(i).getStation_url();
                        Constant.position = i;
                        Constant.Model_fav = mylist.get(i);
                        setmaindata();
                        play();
                        addtorecent(mylist.get(i));
                        check_favorite(mylist.get(i));

                    } else {
                        int i = Constant.position - 1;
                        Constant.station_playing = mylist.get(i).getStation_name();
                        Constant.imageurl = mylist.get(i).getStation_image();
                        Constant.streamurl = mylist.get(i).getStation_url();
                        Constant.position = i;
                        Constant.Model_fav = mylist.get(i);
                        setmaindata();
                        play();
                        addtorecent(mylist.get(i));
                        check_favorite(mylist.get(i));
                    }
                } catch (Exception e) {

                }

            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String sAux = "\nTry This Music application, It's Awesome \n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Share By"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });

        menu_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Main.this, menu_player);
                //inflating menu from xml resource
                popup.inflate(R.menu.menumauin);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.alam:
                                alarm.callOnClick();
                                break;
                            case R.id.sleep:
                                sleep_timer.callOnClick();
                                break;
                            case R.id.nextplay:
                                nextplay.callOnClick();
                                break;
                            case R.id.prevplay:
                                previus_play.callOnClick();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        AudioManager am1 = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        assert am1 != null;
        int result = am1.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
        }
    }

    public void setdata2(long progress) {
        String duration = String.valueOf(progress);
        try {
            long current_duration = Long.parseLong(duration);
            textstart.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) current_duration),
                    TimeUnit.MILLISECONDS.toSeconds((long) current_duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) current_duration))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String a = textend.getText().toString();
        String b = textstart.getText().toString();

        if (a.equalsIgnoreCase(b)) {
            pausetemp();
        } else {
            timer();
        }

    }


    private class GetXMLFromServer extends AsyncTask<String, Void, String> {
        HttpHandler nh;

        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            nh = new HttpHandler();
            InputStream is = nh.CallServer(Constant.api_url);
            if (is != null) {
                res = nh.StreamToString(is);
            } else {
                AssetManager am = getResources().getAssets();
                try
                {
                    is = am.open(Constant.file_name_api);
                    int length = is.available();
                    byte[] data = new byte[length];
                    is.read(data);
                    res = new String(data);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                    res = "NotConnected";
                }


            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("NotConnected")) {
                customtoast("Not Connect To Server");
                Toast.makeText(Main.this, "Not Connect To Server", Toast.LENGTH_SHORT).show();


            } else {
                ParseXML(result);
            }
        }
    }



    public void Showdialoge() {
        final Dialog dialog = new Dialog(this);
        final RelativeLayout layout;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_exit);
        RelativeLayout tv_yes = dialog.findViewById(R.id.tv_yes);
        RelativeLayout tv_no = dialog.findViewById(R.id.tv_no);
        RelativeLayout rate = dialog.findViewById(R.id.rate);

        dialog.show();

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                onDestroy();
                dialog.dismiss();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                dialog.dismiss();

            }
        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void setupviewpager() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final HomePagerAdapter adapter = new HomePagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((HomePagerAdapter) viewPager.getAdapter()).getFragment(position);
                if (fragment != null && position == 0) {
                } else {
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    public static class HomePagerAdapter extends FragmentPagerAdapter {
        private Map<Integer, String> mFragmentTags;
        private WeakReference<Context> mContextRef;
        FragmentManager fragmentManager;

        HomePagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
            mContextRef = new WeakReference<Context>(context);
            mFragmentTags = new HashMap<>();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                assert tag != null;
                mFragmentTags.put(position, tag);

            }
            return obj;
        }

        Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return fragmentManager.findFragmentByTag(tag);
        }


        @Override
        public int getCount() {
            return HomePage.values().length;
        }

        @Override
        public Fragment getItem(int position) {
            HomePage homePage = getWhichHomePage(position);

            switch (homePage) {
                case RECORD:
                    return new Home_fragment();
                case RECORDINGS:
                    return new Favourite_fragment();
                case RECENT:
                    return new Recent_fragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            HomePage homePage = getWhichHomePage(position);

            Context context = mContextRef.get();
            if (context != null) {
                switch (homePage) {
                    case RECORD:
                        return "Home";
                    case RECORDINGS:
                        return "Favorite";
                    case RECENT:
                        return "Recently Played";
                }
            }
            return null;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        private HomePage getWhichHomePage(int position) {
            return HomePage.values()[position];
        }

        private enum HomePage {
            RECORD, RECORDINGS, RECENT
        }
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        FragmentManager manager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.manager = manager;

        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTitleList.get(position);
            if (tag == null)
                return null;
            return manager.findFragmentById(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void RegisterReciever() {
        reciever_playpause = new PlayPause();
        registerReceiver(reciever_playpause, new IntentFilter(RECIEVER_NOTI_PLAYPAUSE));
        registerReceiver(reciever_playpause, new IntentFilter(Constant.Stop_Not));
    }

    private void assignids() {
        radio_name = findViewById(R.id.radio_name);
        snak = findViewById(R.id.snak);
        radio_name2 = findViewById(R.id.radio_name2);
        song_name = findViewById(R.id.song_name);
        song_name2 = findViewById(R.id.song_name2);
        back_image = findViewById(R.id.back_image);
        close_panal = findViewById(R.id.close_panal);
        menu_player = findViewById(R.id.menu_player);
        nextplay = findViewById(R.id.nextplay);
        dragView = findViewById(R.id.dragView);
        stop_recor = findViewById(R.id.stop_recor);
        recyclerview_stations = findViewById(R.id.recyclerview_stations);
        player_icon_lay = findViewById(R.id.player_icon_lay);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerview_stations.setLayoutManager(linearLayoutManager);


        mini_image = findViewById(R.id.mini_image);
        drawer_lay = findViewById(R.id.drawer_lay);
        play_pause_mini = findViewById(R.id.play_pause_mini);
        alarm = findViewById(R.id.alarm);
        play_pause_main = findViewById(R.id.play_pause_main);
        sleep_timer = findViewById(R.id.sleep_timer);
        iv_share = findViewById(R.id.iv_share);
        record = findViewById(R.id.record);
        iv_favourite = findViewById(R.id.iv_favourite);
        search = findViewById(R.id.search);
        drawer_menu = findViewById(R.id.drawer_menu);
        exit = findViewById(R.id.exit);
        previus_play = findViewById(R.id.previus_play);
        circular_image = findViewById(R.id.circular_image);
        circularSeekBar = findViewById(R.id.circularSeekBar);


        home_dr = findViewById(R.id.home_dr);
        fav_dr = findViewById(R.id.fav_dr);
        recent_dr = findViewById(R.id.recent_dr);
        record_dr = findViewById(R.id.record_dr);
        exit_dr = findViewById(R.id.exit_dr);
        privacy_policy = findViewById(R.id.privacy_policy);


        mLayout = findViewById(R.id.sliding_layout);
        mini_player = findViewById(R.id.mini_player);
        conatiner = findViewById(R.id.conatiner);
        timingas = findViewById(R.id.timingas);
        textstart = findViewById(R.id.textstart);
        textend = findViewById(R.id.textend);
        mini_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        dragView.setVisibility(View.GONE);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("onPanelSlide", "onPanelSlide, offset " + slideOffset);
                if (slideOffset > 0.5) {
                    mini_player.setVisibility(View.GONE);
                } else if (slideOffset < 0.5) {
                    mini_player.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("onPanelSlide", "onPanelStateChanged " + newState);
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    mini_player.setVisibility(View.VISIBLE);
                } else if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {

                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    hideKeyboard(Main.this);
                    mini_player.setVisibility(View.GONE);
                } else if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });

        drawer_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.openDrawer(Gravity.START);
            }
        });
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Main.this, MainActivityalarm.class);
                startActivity(in);
            }
        });
        sleep_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Main.this, audio.radiostation.usaradiostations.sleeptimer.MainActivity.class);
                startActivity(in);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showdialoge();

            }
        });
        play_pause_mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_playpause();
            }
        });
        play_pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_playpause();
            }
        });

        home_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);

                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tabLayout.getTabAt(0).select();
                            }
                        }, 100);
            }
        });

        fav_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);

                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tabLayout.getTabAt(1).select();
                            }
                        }, 100);
            }
        });
        recent_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);

                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tabLayout.getTabAt(2).select();
                            }
                        }, 100);
            }
        });
        record_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);

                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tabLayout.getTabAt(3).select();
                            }
                        }, 100);
            }
        });

        exit_dr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);
                Showdialoge();
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_lay.closeDrawer(Gravity.START);

                Intent i = new Intent(Main.this, Policies.class);
                startActivity(i);
            }
        });
        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Log.d("progresschange", progress + "");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        song_name2.setSelected(true);
    }

    public class PlayPause extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(RECIEVER_NOTI_PLAYPAUSE)) {
                handle_playpause();
            } else if (action.equalsIgnoreCase(Constant.Stop_Not)) {
                try {
                    pauseall();
                    clearNotification();
                    Intent startInnt1 = new Intent(Main.this, Notification_Service_class.class);
                    startInnt1.setAction(Constant.ACTION_ENABLE_STICKING);
                    stopService(startInnt1);
                    clearNotification();
                    AudiostreamMetadataManager.getInstance().stop();
                } catch (Exception e) {

                }
            }
        }
    }

    private void pauseall() {
        Services.pausefm();
        play_pause_main.setImageResource(R.drawable.ic_play_arrow);
        play_pause_mini.setImageResource(R.drawable.ic_play_arrow);
        Constant.playpause = false;
        clearNotification();
        Intent startInnt1 = new Intent(Main.this, Notification_Service_class.class);
        startInnt1.setAction(Constant.ACTION_ENABLE_STICKING);
        stopService(startInnt1);
        clearNotification();
    }

    private void pausetemp() {
        Services.pausefm();
        play_pause_main.setImageResource(R.drawable.ic_play_arrow);
        play_pause_mini.setImageResource(R.drawable.ic_play_arrow);
        Constant.playpause = false;

        Intent startInnt1 = new Intent(Main.this, Notification_Service_class.class);
        startInnt1.setAction(Constant.ACTION_ENABLE_STICKING);
        startService(startInnt1);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //mediaVlmSeekBar = (SeekBar) findViewById(R.id.seekBar1);
            int index = circularSeekBar.getProgress();
            if (index == 15) {

            } else {
                circularSeekBar.setProgress(index + 1);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            int index = circularSeekBar.getProgress();

            if (index == 0) {

            } else {
                circularSeekBar.setProgress(index - 1);
            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return super.onKeyUp(keyCode, event);
    }

    private void volumeset() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            circularSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            circularSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle_playpause() {
        if (Constant.isrecording) {
            customtoast("Stop recording first");

//            Toast.makeText(this, "Stop recording first", Toast.LENGTH_SHORT).show();
        } else {

            if (Constant.playpause) {
                Services.pausefm();
                play_pause_main.setImageResource(R.drawable.ic_play_arrow);
                play_pause_mini.setImageResource(R.drawable.ic_play_arrow);
                Constant.playpause = false;
                Intent startIntent1 = new Intent(this, Notification_Service_class.class);
                startIntent1.setAction(Constant.ACTION_ENABLE_STICKING);
                startService(startIntent1);
            } else {
                play();
            }
        }
    }


    public void setmaindata() {

        timingas.setVisibility(View.GONE);
        player_icon_lay.setVisibility(View.VISIBLE);
        radio_name.setText(Constant.station_playing);
        radio_name2.setText(Constant.station_playing);
        song_name.setText("");
        song_name2.setText("");
        setimages();
        play_on_pannal();
        getdata();
    }


    public void setrecdata() {
        AudiostreamMetadataManager.getInstance().stop();
        timingas.setVisibility(View.VISIBLE);
        player_icon_lay.setVisibility(View.GONE);
        radio_name.setText(Constant.station_playing);
        radio_name2.setText(Constant.station_playing);
        Constant.song_name = "";
        song_name.setText(Constant.song_name);
        song_name2.setText(Constant.song_name);

        Glide.with(this).load(R.drawable.appicon).into(circular_image);
        Glide.with(this).load(R.drawable.appicon).into(mini_image);

        if (Constant.imageurl.contains(".gif")) {
            Picasso.with(this).load(R.drawable.appicon).into(back_image);
        } else {
            Picasso.with(this).load(R.drawable.appicon)
                    .transform(new Blur(this, 80)).into(back_image);
        }
        play_on_pannal();
        getdata();
        send_notification();

    }

    public void setdata(String path) {
        String mediaPath = Uri.parse(path).getPath();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mediaPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.d("duration", "" + duration);
        mmr.release();
        try {
            long current_duration = Long.parseLong(duration);
            textend.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) current_duration),
                    TimeUnit.MILLISECONDS.toSeconds((long) current_duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) current_duration))));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void timer() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    long progress = Services.exoPlayer.getCurrentPosition();
                    setdata2(progress);
                }
            }, 200);

        } catch (Exception e) {
            pausetemp();
        }

    }

    private void play_on_pannal() {
        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            dragView.setVisibility(View.VISIBLE);
        }

    }


    public void play() {
        Constant.alarm_string = Constant.station_playing + "|" + Constant.imageurl + "|" + Constant.streamurl;


        Services.initialize(this, Constant.streamurl);
        Constant.playpause = true;
        Intent kkk = new Intent(this, Services.class);
        kkk.setAction(Constant.ACTION_ENABLE_STICKING);
        startService(kkk);
        send_notification();
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        play_pause_main.setImageResource(R.drawable.ic_pause);
        play_pause_mini.setImageResource(R.drawable.ic_pause);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(reciever_playpause);
        AudiostreamMetadataManager.getInstance().stop();
        if (Services.exoPlayer != null) {
            Services.exoPlayer.stop();
            Services.exoPlayer.release();
        }
        clearNotification();
        Intent kkk = new Intent(Main.this, Services.class);
        kkk.setAction(Constant.ACTION_DISABLE_STICKING);
        stopService(kkk);
        Intent startIntent1 = new Intent(Main.this, Notification_Service_class.class);
        startIntent1.setAction(Constant.ACTION_DISABLE_STICKING);
        stopService(startIntent1);
        Log.d("called", "distroy");
        clearNotification();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(901);
        notificationManager.cancelAll();
    }

    public void getdata() {
        if (Constant.streamurl != null && !Constant.streamurl.equals("")) {
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                Uri uri = Uri.parse(Constant.streamurl);

                OnNewMetadataListener listener = new OnNewMetadataListener() {
                    @Override
                    public void onNewHeaders(String stringUri, List<String> name, List<String> desc,
                                             List<String> br, List<String> genre, List<String> info) {

                    }

                    @Override
                    public void onNewStreamTitle(String stringUri, String streamTitle) {
                        String title = "";
                        String artist_nam = "";
                        String album_name = "";
                        Log.d("title2", streamTitle);
                        if (streamTitle.contains("http")) {
                            title = streamTitle.replaceAll("http?://\\S+\\s?", "");

                            Log.d("title", title);
                            if (title.contains("StreamUrl=")) {
                                title = title.replace("StreamUrl=", "");
                                Log.d("title1", title);
                            }
                        } else if (streamTitle.contains("StreamUrl=")) {
                            title = streamTitle.replace("StreamUrl=", "");
                        } else {
                            title = streamTitle;
                        }
                        if (title.contains("-")) {

                            try {
                                String name = title.substring(0, title.indexOf("-") - 1);
                                String name2 = title;

                                Constant.song_name = name2;
                                song_name2.setText(Constant.song_name);
                                song_name.setText(Constant.song_name);
                                lastfmapi(name);
                            /*if (name2.contains("-")) {
                                StringTokenizer tokens = new StringTokenizer(name2, "-");
                                String first = tokens.nextToken();
                                String second = tokens.nextToken();

                            } else {

                            }*/
                                Log.d("imageurl", name);
                                send_notification();
                            } catch (Exception e) {

                            }

                        } else if (title.isEmpty()) {
                            Constant.song_name = "Unknown...";
                            song_name2.setText(Constant.song_name);
                            song_name.setText(Constant.song_name);
                            Log.d("imageurl", "error in fetching data");
                        } else {
                            Constant.song_name = title;
                            song_name2.setText(Constant.song_name);
                            song_name.setText(Constant.song_name);
                        }
                    }
                };
                AudiostreamMetadataManager.getInstance()
                        .setUri(uri)
                        .setOnNewMetadataListener((com.vodyasov.amr.OnNewMetadataListener) listener)
                        .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
                        .start();
            } else {
            }
        }
    }

    private void send_notification() {
        Intent startIntent1 = new Intent(this, Notification_Service_class.class);
        startIntent1.setAction(Constant.ACTION_ENABLE_STICKING);
        startService(startIntent1);
    }

    public void lastfmapi(String name) {
        RequestQueue queue = Volley.newRequestQueue(Main.this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Constant.LASTFM + name + Constant.LASTFMSUB,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TagChat", response + "");
                String view, time, hunter, message, avatar, target, status, abc, bcd;
                try {
                    JSONObject jsonobj = response.getJSONObject("artist");
                    JSONArray jsonArray = jsonobj.getJSONArray("image");
                    JSONObject objJson = null;
                    objJson = jsonArray.getJSONObject(3);
                    String imageurl = objJson.getString("#text");
                    if (imageurl.equalsIgnoreCase("") || Constant.imageurl == null) {
                    } else {
                        Constant.imageurl = imageurl;
                        setimages();
                        send_notification();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsObjRequest);
    }

    private void setimages() {
        try {
            if (Constant.imageurl.contains(".gif")) {
                Picasso.with(this).load(Constant.imageurl).into(back_image);
            } else {
                Picasso.with(this).load(Constant.imageurl)
                        .transform(new Blur(this, 80)).into(back_image);
            }
            Glide.with(this).load(Constant.imageurl).into(circular_image);
            Glide.with(this).load(Constant.imageurl).into(mini_image);
        } catch (Exception e) {

        }

    }

    public void search(String visibility) {
        if (visibility.equalsIgnoreCase("visible")) {
            conatiner.setVisibility(View.VISIBLE);
            Constant.fragopen = "srch";

        } else if (visibility.equalsIgnoreCase("gone")) {
            conatiner.setVisibility(View.GONE);
            Constant.fragopen = "home";

        }
    }

    public  void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void ParseXML(String xmlString) {
        mylist.clear();
        try {


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    String stationname = parser.getAttributeValue(null, "name");
                    String image = parser.getAttributeValue(null, "image");
                    String link = parser.getAttributeValue(null, "link");
                    if (name.equalsIgnoreCase("station")) {
                        mylist.add(new Model_class(stationname, link, image));
                        Log.d("parsexml", "size" + mylist.size());
                        Player_adapter home_adapter = new Player_adapter(this, mylist);
                        recyclerview_stations.setAdapter(home_adapter);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.d("parsexml", "Error in ParseXML()", e);
        }
    }

    public void check_favorite(Model_class cat) {
        if (Favorite_save_Prefrence.getInstance(this).isFavorite(cat)) {
            iv_favourite.setImageResource(R.drawable.ic_favorite_fill);
        } else {
            iv_favourite.setImageResource(R.drawable.ic_favorite_empty);
        }
    }

    public void addtorecent(Model_class list) {
        if (Recent_list.getInstance(Main.this).isFavorite(Constant.Model_fav)) {
            Recent_list.getInstance(Main.this).removeFavorite(Main.this, Constant.Model_fav);
            Recent_list.getInstance(Main.this).addFavorite(Main.this, Constant.Model_fav);
        } else {
            Recent_list.getInstance(Main.this).addFavorite(Main.this, Constant.Model_fav);
        }
    }


    class C02211 implements View.OnClickListener {
        C02211() {
        }

        @SuppressLint({"StaticFieldLeak", "WrongConstant"})
        public void onClick(View v) {
            boolean check = checkWriteExternalPermission();
            if (check) {
                if (Constant.playpause) {
                    customtoast("Recording started");

//                    Toast.makeText(Main.this, "Recording started", 0).show();
                    record.setVisibility(View.GONE);
                    stop_recor.setVisibility(View.VISIBLE);
                    Constant.isrecording = true;
                    new C02201().execute(new Void[0]);
                } else if (!Constant.playpause) {
                    customtoast("Play Music Track First");

//                    Toast.makeText(Main.this, "Play Music Track First", Toast.LENGTH_SHORT).show();
                }
            } else if (!check) {
                int requestCode = 200;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(String.valueOf(permissions))
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permissions, requestCode);
                    }
                }
            }
        }

        @SuppressLint("StaticFieldLeak")
        class C02201 extends AsyncTask<Void, Void, StringBuilder> {
            C02201() {
            }

            protected StringBuilder doInBackground(Void... voids) {
                String LOG_TAG = "TAG_ofcurrentdata";
                String resulr = "Done";
                try {
                    File file = new File(Constant.DOWNLOAD_PATH);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    StringBuilder outputSource = new StringBuilder();
                    outputSource.append(file);
                    final int random = new Random().nextInt(1048) + 2048;
                    outputSource.append("/" + song_name.getText().toString().trim() + random + ".mp3");
                    String os;
                    os = outputSource.toString();
                    inputStream = new URL(Constant.streamurl).openStream();
                    Log.d(LOG_TAG, "url.openStream()");
                    fileOutputStream = new FileOutputStream(os);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("FileOutputStream: ");
                    stringBuilder.append(os);
                    Log.d(LOG_TAG, stringBuilder.toString());
                    while (true) {
                        int l;
                        byte[] buffer = new byte[256];
                        Log.d("buffer.print", "" + buffer);
                        while ((l = inputStream.read(buffer)) != -1) {
                            Log.d(LOG_TAG, "in while" + buffer);
                            Log.d(LOG_TAG, "in while" + l);
                            fileOutputStream.write(buffer, 0, l);
                        }
                    }
                } catch (Exception e) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    Log.d(LOG_TAG, "in catch" + e);
                    stringBuilder3.append("");
                    stringBuilder3.append("");
                    return stringBuilder3;
                }
            }

            @SuppressLint("WrongConstant")
            protected void onPostExecute(StringBuilder s) {
                super.onPostExecute(s);
                Context context = Main.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Done");
                stringBuilder.append(s);
                Toast.makeText(context, stringBuilder.toString(), 1).show();
                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(String.valueOf(s)))));
            }
        }
    }


    private boolean checkWriteExternalPermission() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void customtoast(String text) {
        Snackbar snackbar = Snackbar
                .make(snak, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
