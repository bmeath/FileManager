package com.bmeath.filemanager;

import android.content.SharedPreferences;

/**
 * Created by bm on 11/2/17.
 *
 * Provides a means of access to user preferences from anywhere in the app
 */

public class Settings {
    private static Settings instance = null;

    private SharedPreferences sharedPrefs = null;

    /* user preferences */
    public boolean showHidden;  // whether to show hidden files/folders

    private Settings() {
    }

    public static synchronized Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /* apply the user preferences from the SharedPreferences object */
    public void init(SharedPreferences sp)
    {
        this.sharedPrefs = sp;
    }

    /* load settings after starting/resuming
     * returns false if sharedPrefs hasn't been instantiated yet, true otherwise
     */
    public boolean load() {
        if (sharedPrefs != null) {
            showHidden = sharedPrefs.getBoolean("showHidden", false);
            return true;
        } else {
            return false;
        }
    }

    /* save settings after pausing/exiting
     * returns false if sharedPrefs hasn't been instantiated yet, true otherwise
     */
    public boolean save() {
        if (sharedPrefs != null) {
            SharedPreferences.Editor prefEdit = sharedPrefs.edit();

            prefEdit.putBoolean("showHidden", showHidden);

            prefEdit.apply();
            return true;
        } else {
            return false;
        }
    }
}