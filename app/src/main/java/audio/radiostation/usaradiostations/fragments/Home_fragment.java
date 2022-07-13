package audio.radiostation.usaradiostations.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import audio.radiostation.usaradiostations.AdsUtilities;
import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.adapters.Home_adapter;
import audio.radiostation.usaradiostations.constant.Constant;
import audio.radiostation.usaradiostations.constant.HttpHandler;
import audio.radiostation.usaradiostations.model_class.Model_class;
import audio.radiostation.usaradiostations.services.Services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recycler_main;
    ArrayList<Model_class> mylist = new ArrayList();
    FragmentActivity fragmentActivity;
    SwipeRefreshLayout swipe_refresh;
    private Activity mActivity;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        assignid(view);
        HwAds.init(getContext());

        AdsUtilities.CLICK_COUNT(mContext).loadFullScreenAd(mActivity);
        return view;
    }

    // Ads

        // End Ads



    private void assignid(View view) {
        recycler_main = view.findViewById(R.id.recycler_main);
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(fragmentActivity,2, GridLayoutManager.VERTICAL, false);
        recycler_main.setLayoutManager(linearLayoutManager);
        new GetXMLFromServer().execute();
        swipe_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onAttach(Context context) {
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {
        new GetXMLFromServer().execute();

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

                    Toast.makeText(getContext(), ""+e1, Toast.LENGTH_SHORT).show();
                    res = "NotConnected";
                }
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("NotConnected")) {
                swipe_refresh.setRefreshing(false);

                Toast.makeText(fragmentActivity, "Not Connect To Server", Toast.LENGTH_SHORT).show();
            } else {
                ParseXML(result);
            }
        }
    }


    public void ParseXML(String xmlString) {
        mylist.clear();
        swipe_refresh.setRefreshing(true);

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
                    if (name.equalsIgnoreCase("station"))
                    {
                        mylist.add(new Model_class(stationname, link, image));

                        Log.d("parsexml", "size" + mylist.size());
                        Home_adapter home_adapter = new Home_adapter(fragmentActivity,mylist);
                        recycler_main.setAdapter(home_adapter);
                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                }
                eventType = parser.next();
            }
            swipe_refresh.setRefreshing(false);

        } catch (Exception e) {
            Log.d("parsexml", "Error in ParseXML()", e);
        }
        swipe_refresh.setRefreshing(false);

    }
}