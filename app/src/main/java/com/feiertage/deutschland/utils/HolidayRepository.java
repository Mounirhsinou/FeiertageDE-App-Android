package com.feiertage.deutschland.utils;

import android.content.Context;
import android.util.Log;

import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.models.HolidayDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * HolidayRepository – single source of truth for holiday data.
 * Reads from the bundled JSON assets and provides filtered, sorted lists.
 */
public class HolidayRepository {

    private static final String TAG = "HolidayRepository";
    private static final String ASSET_FOLDER = "holidays";

    // Singleton
    private static HolidayRepository instance;

    private final Context context;
    private final Gson gson = new Gson();

    // In-memory cache: year → list
    private final java.util.Map<Integer, List<Holiday>> cache = new java.util.HashMap<>();

    private HolidayRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public static HolidayRepository getInstance(Context context) {
        if (instance == null) {
            instance = new HolidayRepository(context);
        }
        return instance;
    }

    // ─── Core data loading ────────────────────────────────────────────────────

    /**
     * Load all holidays for the given year, with in-memory caching.
     */
    public List<Holiday> getHolidaysForYear(int year) {
        if (cache.containsKey(year)) {
            return cache.get(year);
        }
        List<Holiday> holidays = loadFromAsset(year);
        cache.put(year, holidays);
        return holidays;
    }

    /**
     * Reads the JSON file from assets/holidays/holidays_YYYY.json and
     * parses it into a list of Holiday objects, sorted by date ascending.
     */
    private List<Holiday> loadFromAsset(int year) {
        String fileName = ASSET_FOLDER + "/holidays_" + year + ".json";
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            HolidayDatabase db = gson.fromJson(json, HolidayDatabase.class);
            if (db != null && db.getHolidays() != null) {
                List<Holiday> list = db.getHolidays();
                // Sort by date ascending
                Collections.sort(list, (a, b) -> a.getDate().compareTo(b.getDate()));
                return list;
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load holidays for year " + year + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // ─── Filtering helpers ────────────────────────────────────────────────────

    /**
     * Filter holidays by state code. "ALL" returns all holidays.
     */
    public List<Holiday> getHolidaysForState(int year, String stateCode) {
        List<Holiday> all = getHolidaysForYear(year);
        if ("ALL".equals(stateCode)) return new ArrayList<>(all);
        List<Holiday> filtered = new ArrayList<>();
        for (Holiday h : all) {
            if (h.appliesInState(stateCode)) {
                filtered.add(h);
            }
        }
        return filtered;
    }

    /**
     * Search holidays by name (case-insensitive) within a year/state.
     */
    public List<Holiday> searchHolidays(int year, String stateCode, String query) {
        List<Holiday> source = getHolidaysForState(year, stateCode);
        if (query == null || query.isEmpty()) return source;
        String lower = query.toLowerCase();
        List<Holiday> results = new ArrayList<>();
        for (Holiday h : source) {
            if (h.getName().toLowerCase().contains(lower)
                    || (h.getNameEn() != null && h.getNameEn().toLowerCase().contains(lower))) {
                results.add(h);
            }
        }
        return results;
    }

    /**
     * Returns the next upcoming holiday from today for the given state.
     */
    public Holiday getNextHoliday(String stateCode) {
        String today = DateUtils.getTodayIso();
        int year = DateUtils.getCurrentYear();

        // Check current year first, then next year
        for (int y = year; y <= year + 1; y++) {
            List<Holiday> list = getHolidaysForState(y, stateCode);
            for (Holiday h : list) {
                if (h.getDate().compareTo(today) >= 0) {
                    return h;
                }
            }
        }
        return null;
    }

    /**
     * Returns holidays that are today for the given state.
     */
    public List<Holiday> getTodayHolidays(String stateCode) {
        String today = DateUtils.getTodayIso();
        int year = DateUtils.getCurrentYear();
        List<Holiday> result = new ArrayList<>();
        for (Holiday h : getHolidaysForState(year, stateCode)) {
            if (h.getDate().equals(today)) {
                result.add(h);
            }
        }
        return result;
    }

    /**
     * Returns holidays for a specific month (1-based) and year.
     */
    public List<Holiday> getHolidaysForMonth(int year, int month, String stateCode) {
        String monthStr = String.format("%04d-%02d", year, month);
        List<Holiday> result = new ArrayList<>();
        for (Holiday h : getHolidaysForState(year, stateCode)) {
            if (h.getDate().startsWith(monthStr)) {
                result.add(h);
            }
        }
        return result;
    }

    /**
     * Get national-only holidays for a year.
     */
    public List<Holiday> getNationalHolidays(int year) {
        List<Holiday> result = new ArrayList<>();
        for (Holiday h : getHolidaysForYear(year)) {
            if (h.isNational()) result.add(h);
        }
        return result;
    }

    /**
     * Clear the in-memory cache (call if memory pressure detected).
     */
    public void clearCache() {
        cache.clear();
    }
}
