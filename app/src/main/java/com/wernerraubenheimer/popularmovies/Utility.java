package com.wernerraubenheimer.popularmovies;

/**
 * Created by wernerr on 2017/06/24.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_title_ringtone),
                context.getString(R.string.pref_title_ringtone));
    }
}
