package com.feiertage.deutschland.models;

/**
 * Model representing a German federal state (Bundesland).
 */
public class GermanState {

    private String code;       // Short code (e.g., "BY", "NW")
    private String name;       // Full German name (e.g., "Bayern")
    private String capital;    // State capital city
    private int holidayCount;  // Number of public holidays (runtime computed)
    private boolean isSelected;// User selected filter state

    public GermanState() {}

    public GermanState(String code, String name, String capital) {
        this.code = code;
        this.name = name;
        this.capital = capital;
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public String getCode()       { return code; }
    public String getName()       { return name; }
    public String getCapital()    { return capital; }
    public int getHolidayCount()  { return holidayCount; }
    public boolean isSelected()   { return isSelected; }

    // ─── Setters ────────────────────────────────────────────────────────────────

    public void setCode(String code)            { this.code = code; }
    public void setName(String name)            { this.name = name; }
    public void setCapital(String capital)      { this.capital = capital; }
    public void setHolidayCount(int count)      { this.holidayCount = count; }
    public void setSelected(boolean selected)   { this.isSelected = selected; }

    @Override
    public String toString() {
        return "GermanState{code='" + code + "', name='" + name + "'}";
    }
}
