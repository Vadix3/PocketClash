package com.example.pocketclash;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MySP {

    public interface KEYS {
        public static final String TOP_10_ARRAY = "TOP_10_ARRAY";
    }

    private SharedPreferences myPrefs;

    public MySP() {
    }

    public MySP(Context context) {
        this.myPrefs = context.getSharedPreferences("MY_SP", Context.MODE_PRIVATE);
    }

    /**
     * A method to get at string from SP
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String def) {
        return myPrefs.getString(key, def);
    }
}
