package app.co.francisco.co_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 20/03/2019.
 */

public class Preferences {
    public Context context;
    public Preferences(Context ctx){
        this.context=ctx;
    }

    public String get_preference(String cle,String nom_preference){
        SharedPreferences sharedPreferences = context.getSharedPreferences(nom_preference, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(cle,"null");
        return value;
    }
    public  void set_preference(String cle,String valeur, String nom_preference){
        SharedPreferences sharedPreferences = context.getSharedPreferences(nom_preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cle, valeur );
        editor.apply();
    }
}
