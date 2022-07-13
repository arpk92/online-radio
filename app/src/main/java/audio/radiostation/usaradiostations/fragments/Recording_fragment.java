package audio.radiostation.usaradiostations.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import audio.radiostation.usaradiostations.R;
import audio.radiostation.usaradiostations.adapters.Music_adapter;
import audio.radiostation.usaradiostations.constant.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class Recording_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<File> Mypdf = new ArrayList<File>();
    FragmentActivity fragmentActivity;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView recycler_main;
    TextView no_list;

    private String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.INTERNET",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_fragment, container, false);
        assignids(view);


        boolean check = checkWriteExternalPermission();


        if (check) {
            generalatworkin();

        } else if (!check) {
            int requestCode = 200;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fragmentActivity.checkSelfPermission(String.valueOf(permissions))
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, requestCode);
                }
            }
        }
        return view;
    }

    private boolean checkWriteExternalPermission() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = fragmentActivity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume_called", "resume_called");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();

            }
        }, 200 );   }

    private void generalatworkin() {


        Mypdf.clear();
        File file = new File(Constant.DOWNLOAD_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "audio recording";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        Log.d("Files", "Size: " + files.length);
        if (files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                Mypdf.add(files[i].getAbsoluteFile());
                Log.d("lmjkbn", "" + Mypdf.get(i).getAbsolutePath().getBytes());
            }
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recycler_main.setLayoutManager(mLayoutManager);

            Collections.sort(Mypdf, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    String o1 = String.valueOf((object1.lastModified()));
                    String o2 = String.valueOf((object2.lastModified()));
                    return o2.compareTo(o1);
                }
            });

            Music_adapter adapter = new Music_adapter(fragmentActivity, (ArrayList<File>) Mypdf);
            recycler_main.setAdapter(adapter);
        } else {
            no_list.setVisibility(View.VISIBLE);
            no_list.setText("No Recordings Found");
        }
        swipe_refresh.setRefreshing(false);
    }

    private void assignids(View view) {
        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        no_list = view.findViewById(R.id.no_list);
        recycler_main = view.findViewById(R.id.recycler_main);
        swipe_refresh.setRefreshing(true);
        swipe_refresh.setOnRefreshListener(this);
    }

    @Override
    public void onAttach(Context context) {
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {


        boolean check = checkWriteExternalPermission();


        if (check) {
            generalatworkin();
        } else if (!check) {
            int requestCode = 200;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fragmentActivity.checkSelfPermission(String.valueOf(permissions))
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, requestCode);
                }
            }
        }
    }
}
