package com.example.phishdetector.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.phishdetector.model.ScanHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferenceHelper {

    private static final String PREF_NAME = "PhishDetectorPrefs";
    private static final String KEY_HISTORY = "scan_history";
    private static final int MAX_HISTORY = 20;

    private final SharedPreferences prefs;
    private final Gson gson;

    public PreferenceHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveScan(ScanHistory scan) {
        List<ScanHistory> history = getHistory();
        history.add(0, scan); // newest first

        // Keep only last MAX_HISTORY items
        if (history.size() > MAX_HISTORY) {
            history = history.subList(0, MAX_HISTORY);
        }

        String json = gson.toJson(history);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }

    public List<ScanHistory> getHistory() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<ScanHistory>>() {}.getType();
        List<ScanHistory> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}
