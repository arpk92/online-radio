package audio.radiostation.usaradiostations.clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import audio.radiostation.usaradiostations.model_class.Model_class;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ST_004 on 27-04-2017.
 */

public class Favorite_save_Prefrence {
    public static final String FAVORITES = "favorite";
    private static Favorite_save_Prefrence instance = null;
    private Context context;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;
    private Favorite_save_Prefrence(Context context) {
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


    public static Favorite_save_Prefrence getInstance(Context context) {
        if (instance == null || instance.context == null) {
            instance = new Favorite_save_Prefrence(context);
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
        List<Model_class> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Model_class>();
        favorites.add(model);
        saveFavorites(context, favorites);
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
        Collections.reverse(favorites);
        return (ArrayList<Model_class>) favorites;

    }

    public void removeFavorite(Context context, Model_class model) {
        List<Model_class> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(model);
            Log.d("favorite lis", favorites.toString());
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
