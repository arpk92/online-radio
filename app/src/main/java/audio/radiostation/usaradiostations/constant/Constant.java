package audio.radiostation.usaradiostations.constant;

import android.os.Environment;
import audio.radiostation.usaradiostations.model_class.Model_class;

import java.util.ArrayList;

public class Constant {

    public  static  String api_url = "";

    //ad count for interstitial ads
    public static int count = 2;


    //enter here the correct name of your file in assets folder
    public  static  String file_name_api = "RadioDeutschland.xml";

    public static String ACTION_ENABLE_STICKING="play";
    public static String ACTION_DISABLE_STICKING="pause";
    public static String name="";
    public static boolean NOTIFICATION_CREATED=false;
    public static boolean playpause=false;
    public static String Stop_Not="destroy_player";
    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" +"audio recording";
    public static boolean isrecording=false;


    public static String imageurl="";
    public static String fragopen="home";
    public static String song_name="";
    public static String station_playing="";
    public static int position = 0;

    public static String privacy_policy_url="https://radiobestapps.wordpress.com/politicas-de-privacidad/";

    public static String streamurl="";
    public static String alarm_string="";
    public static ArrayList<Model_class> recent_list = new ArrayList<>() ;

    public static Model_class Model_fav;

    public static String LASTFMSUB = "&api_key=2b35547bd5675d8ecb2b911ee9901f59&format=json";
    public static String LASTFM = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=";
}
