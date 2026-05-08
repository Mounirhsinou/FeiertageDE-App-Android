package com.feiertage.deutschland.utils;

import com.feiertage.deutschland.models.GermanState;

import java.util.ArrayList;
import java.util.List;

/**
 * Static registry of all 16 German federal states (Bundesländer).
 * Provides helper lookups by code or name.
 */
public class StateManager {

    private static final List<GermanState> ALL_STATES = new ArrayList<>();

    static {
        ALL_STATES.add(new GermanState("BW", "Baden-Württemberg",    "Stuttgart"));
        ALL_STATES.add(new GermanState("BY", "Bayern",               "München"));
        ALL_STATES.add(new GermanState("BE", "Berlin",               "Berlin"));
        ALL_STATES.add(new GermanState("BB", "Brandenburg",          "Potsdam"));
        ALL_STATES.add(new GermanState("HB", "Bremen",               "Bremen"));
        ALL_STATES.add(new GermanState("HH", "Hamburg",              "Hamburg"));
        ALL_STATES.add(new GermanState("HE", "Hessen",               "Wiesbaden"));
        ALL_STATES.add(new GermanState("MV", "Mecklenburg-Vorpommern","Schwerin"));
        ALL_STATES.add(new GermanState("NI", "Niedersachsen",        "Hannover"));
        ALL_STATES.add(new GermanState("NW", "Nordrhein-Westfalen",  "Düsseldorf"));
        ALL_STATES.add(new GermanState("RP", "Rheinland-Pfalz",      "Mainz"));
        ALL_STATES.add(new GermanState("SL", "Saarland",             "Saarbrücken"));
        ALL_STATES.add(new GermanState("SN", "Sachsen",              "Dresden"));
        ALL_STATES.add(new GermanState("ST", "Sachsen-Anhalt",       "Magdeburg"));
        ALL_STATES.add(new GermanState("SH", "Schleswig-Holstein",   "Kiel"));
        ALL_STATES.add(new GermanState("TH", "Thüringen",            "Erfurt"));
    }

    private StateManager() {}

    /** Returns an unmodifiable list of all 16 states. */
    public static List<GermanState> getAllStates() {
        return new ArrayList<>(ALL_STATES);
    }

    /** Find a state by its two-letter code (case-insensitive). Returns null if not found. */
    public static GermanState getByCode(String code) {
        if (code == null) return null;
        for (GermanState s : ALL_STATES) {
            if (s.getCode().equalsIgnoreCase(code)) return s;
        }
        return null;
    }

    /** Find a state by its full German name. Returns null if not found. */
    public static GermanState getByName(String name) {
        if (name == null) return null;
        for (GermanState s : ALL_STATES) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    /** Returns the display name for a state code, or the code itself as fallback. */
    public static String getDisplayName(String code) {
        if ("ALL".equalsIgnoreCase(code)) return "Alle Bundesländer";
        GermanState s = getByCode(code);
        return s != null ? s.getName() : code;
    }

    /** Returns number of states where holidays count > threshold. */
    public static int countNationalStates(List<String> stateCodes) {
        return (stateCodes == null) ? 0 : stateCodes.size();
    }
}
