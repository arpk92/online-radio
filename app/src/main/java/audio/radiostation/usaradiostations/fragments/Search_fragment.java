package audio.radiostation.usaradiostations.fragments;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import audio.radiostation.usaradiostations.Main;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.adapters.Home_adapter;
import audio.radiostation.usaradiostations.constant.Constant;
import audio.radiostation.usaradiostations.constant.HttpHandler;
import audio.radiostation.usaradiostations.model_class.Model_class;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_fragment extends Fragment {
    RecyclerView recycler_main;
    ArrayList<Model_class> mylist = new ArrayList();
    FragmentActivity fragmentActivity;
//    SwipeRefreshLayout swipe_refresh;
    Home_adapter home_adapter;
    EditText search_edit;
    ImageView back_arrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        assignid(view);


        return view;
    }


    private void assignid(View view) {
        recycler_main = view.findViewById(R.id.recycler_main);
        recycler_main.setNestedScrollingEnabled(false);
//        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        search_edit = view.findViewById(R.id.search_edit);
        back_arrow = view.findViewById(R.id.back_arrow);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(fragmentActivity,2, GridLayoutManager.VERTICAL, false);
        recycler_main.setLayoutManager(linearLayoutManager);
        new GetXMLFromServer().execute();

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((Main)fragmentActivity).search("gone");
                ((Main)fragmentActivity).hideKeyboard(fragmentActivity);

            }
        });
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (home_adapter != null) home_adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);
    }

    private class GetXMLFromServer extends AsyncTask<String, Void, String> {
        HttpHandler nh;
        @Override
        protected String doInBackground(String... strings) {
            String URL = Constant.api_url;
            String res = "";
            nh = new HttpHandler();
            InputStream is = nh.CallServer(URL);
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
                Toast.makeText(fragmentActivity, "Not Connect To Server", Toast.LENGTH_SHORT).show();
            } else {
                ParseXML(result);
            }
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
                    if (name.equalsIgnoreCase("station"))
                    {
                        mylist.add(new Model_class(stationname, link, image));
                        Log.d("parsexml", "size" + mylist.size());
                         home_adapter = new Home_adapter(fragmentActivity,mylist);
                        recycler_main.setAdapter(home_adapter);
                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.d("parsexml", "Error in ParseXML()", e);
        }

    }
}
