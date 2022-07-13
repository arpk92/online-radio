package audio.radiostation.usaradiostations.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.adapters.Favorite_adapter;
import audio.radiostation.usaradiostations.clases.Favorite_save_Prefrence;
import audio.radiostation.usaradiostations.model_class.Model_class;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favourite_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recycler_main;
    FragmentActivity fragmentActivity;
    SwipeRefreshLayout swipe_refresh;
    Favorite_save_Prefrence sharedPreference;
    TextView no_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        assignid(view);

        return view;
    }



    private void assignid(View view) {
        recycler_main = view.findViewById(R.id.recycler_main);
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        no_list = view.findViewById(R.id.no_list);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(fragmentActivity, 2, GridLayoutManager.VERTICAL, false);
        recycler_main.setLayoutManager(linearLayoutManager);
        swipe_refresh.setColorSchemeResources(R.color.black, R.color.black, R.color.black);
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.setRefreshing(true);

        checkfavo();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume_called", "resume_called");

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                onRefresh();


            }
        }, 200 );
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "onStart");
    }

    private void checkfavo() {
        List<Model_class> mylist = new ArrayList();
        mylist.clear();
        mylist = sharedPreference.getInstance(getContext()).getFavorites(fragmentActivity);
        Log.d("dkjvo", "called");
        if (mylist == null) {
            //Toast.makeText(getContext(), "Nothing In the List", Toast.LENGTH_SHORT).show();
            swipe_refresh.setRefreshing(false);
            no_list.setVisibility(View.VISIBLE);
        } else {
            if (mylist.size() == 0) {
                //Toast.makeText(getContext(), "Nothing in the list", Toast.LENGTH_SHORT).show();
                Favorite_adapter home_adapter = new Favorite_adapter(fragmentActivity, mylist);
                recycler_main.setAdapter(home_adapter);

                no_list.setVisibility(View.VISIBLE);
                swipe_refresh.setRefreshing(false);
            } else {
                if (mylist != null) {
                    try {

                        no_list.setVisibility(View.GONE);
                        Favorite_adapter home_adapter = new Favorite_adapter(fragmentActivity, mylist);
                        recycler_main.setAdapter(home_adapter);

                        swipe_refresh.setRefreshing(false);
                    } catch (Exception e) {
                    }
                }
            }
        }
        swipe_refresh.setRefreshing(false);
    }

    @Override
    public void onAttach(Context context) {
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);

    }

    @Override
    public void onRefresh() {
        checkfavo();
    }
}
