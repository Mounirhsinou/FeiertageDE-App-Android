package com.feiertage.deutschland.models;

import java.io.Serializable;
import java.util.List;

/**
 * Model representing a single public holiday (Feiertag).
 */
public class Holiday implements Serializable {

    private String id;           // Unique identifier (e.g., "2026_neujahr")
    private String name;         // Holiday name in German
    private String nameEn;       // Holiday name in English
    private String date;         // ISO date string "YYYY-MM-DD"
    private boolean isNational;  // True if observed in all 16 states
    private List<String> states; // List of state codes where it applies (e.g., ["BY", "BW"])
    private String description;  // Short description of the holiday
    private String type;         // "national", "state", "regional"
    private int year;            // Year of occurrence
    private boolean isFavorite;  // User-marked favorite (runtime only, stored in SharedPreferences)

    // Empty constructor for Gson
    public Holiday() {}

    public Holiday(String id, String name, String nameEn, String date,
                   boolean isNational, List<String> states, String description,
                   String type, int year) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.date = date;
        this.isNational = isNational;
        this.states = states;
        this.description = description;
        this.type = type;
        this.year = year;
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getNameEn()      { return nameEn; }
    public String getDate()        { return date; }
    public boolean isNational()    { return isNational; }
    public List<String> getStates(){ return states; }
    public String getDescription() { return description; }
    public String getType()        { return type; }
    public int getYear()           { return year; }
    public boolean isFavorite()    { return isFavorite; }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setId(String id)                    { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setNameEn(String nameEn)            { this.nameEn = nameEn; }
    public void setDate(String date)                { this.date = date; }
    public void setNational(boolean national)       { isNational = national; }
    public void setStates(List<String> states)      { this.states = states; }
    public void setDescription(String description)  { this.description = description; }
    public void setType(String type)                { this.type = type; }
    public void setYear(int year)                   { this.year = year; }
    public void setFavorite(boolean favorite)       { isFavorite = favorite; }

    // ─── Utility ─────────────────────────────────────────────────────────────

    /**
     * Returns true if this holiday applies in the given state code.
     */
    public boolean appliesInState(String stateCode) {
        if (isNational) return true;
        if (states == null) return false;
        for (String s : states) {
            if (s.equalsIgnoreCase(stateCode)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Holiday{name='" + name + "', date='" + date + "', national=" + isNational + "}";
    }
}
