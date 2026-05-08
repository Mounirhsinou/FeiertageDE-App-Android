package com.feiertage.deutschland.models;

import java.util.List;

/**
 * Root wrapper model that maps to the top-level holidays JSON file.
 * Used by Gson to deserialize assets/holidays/holidays_YYYY.json
 */
public class HolidayDatabase {

    private String version;
    private String lastUpdated;
    private List<Holiday> holidays;

    public HolidayDatabase() {}

    public String getVersion()            { return version; }
    public String getLastUpdated()        { return lastUpdated; }
    public List<Holiday> getHolidays()   { return holidays; }

    public void setVersion(String version)            { this.version = version; }
    public void setLastUpdated(String lastUpdated)    { this.lastUpdated = lastUpdated; }
    public void setHolidays(List<Holiday> holidays)  { this.holidays = holidays; }
}
