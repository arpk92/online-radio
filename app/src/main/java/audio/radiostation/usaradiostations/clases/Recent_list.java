package audio.radiostation.usaradiostations.clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import audio.radiostation.usaradiostations.model_class.Model_class;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ST_004 on 27-04-2017.
 */

public class Recent_list {
    public static final String FAVORITES = "recent_list";
    private static Recent_list instance = null;
    private Context context;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private Recent_list(Context context) {
        if (context == null)
            return;
        mPreferences = context.getSharedPreferences(FAVORITES, Context.MODE_PRIVATE);
        // mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }


    public void clear() {
        editor = mPreferences.edit();
        editor.clear();
        editor.apply();
    }


    public static Recent_list getInstance(Context context) {
        if (instance == null || instance.context == null) {
            instance = new Recent_list(context);
        }
        return instance;
    }


    public void saveFavorites(Context context, List<Model_class> favorites) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor = mPreferences.edit();
        editor.putString(FAVORITES, jsonFavorites);
        editor.apply();
    }

    public void addFavorite(Context context, Model_class model) {
        List<Model_class> favorites = getFavorites2(context);
        if (favorites == null)
            favorites = new ArrayList<Model_class>();
        favorites.add(model);
        saveFavorites(context, favorites);

        Log.d("myfavsize",""+favorites.size());
        if (favorites.size()>10)
        {
            List<Model_class> favor = getFavorites2(context);
            Log.d("myfavsize","in remove"+favorites.size());
            if (favor != null) {
                favor.remove(0);
                Log.d("myfavsize","in remove"+favor.size());
                saveFavorites(context, favor);
            }
        }
    }

    public List<Model_class> getFavorites(Context context) {
        List<Model_class> favorites;
        if (mPreferences.contains(FAVORITES)) {
            String jsonFavorites = mPreferences.getString(FAVORITES, null);
            Gson gson = new Gson();
            Model_class[] favoriteItems = gson.fromJson(jsonFavorites, Model_class[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Model_class>(favorites);
        } else
            return null;
        return (ArrayList<Model_class>) favorites;

    }

    public List<Model_class> getFavorites2(Context context) {
        List<Model_class> favorites;
        if (mPreferences.contains(FAVORITES)) {
            String jsonFavorites = mPreferences.getString(FAVORITES, null);
            Gson gson = new Gson();
            Model_class[] favoriteItems = gson.fromJson(jsonFavorites, Model_class[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Model_class>(favorites);
        } else
            return null;
        return (ArrayList<Model_class>) favorites;

    }

    public void removeFavorite(Context context, Model_class model) {
        List<Model_class> favorites = getFavorites2(context);
        Log.d("myfavsize","in remove"+favorites.size());

        if (favorites != null) {
            favorites.remove(model);
            Log.d("favorite lis", favorites.toString());
            Log.d("myfavsize","in remove"+favorites.size());
            saveFavorites(context, favorites);
        }
    }


    public boolean isFavorite(Model_class model) {
        ArrayList<Model_class> favo = (ArrayList<Model_class>) getFavorites(context);
        boolean fav = false;
        if (favo == null) {
        } else {
            if (favo.contains(model))
                fav = true;
            else
                fav = false;
        }
        return fav;
    }
}
