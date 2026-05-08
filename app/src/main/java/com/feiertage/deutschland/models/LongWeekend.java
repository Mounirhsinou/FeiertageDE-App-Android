package com.feiertage.deutschland.models;

import java.util.List;

/**
 * Represents a detected long-weekend opportunity, including the holiday,
 * bridge days needed, and total days off.
 */
public class LongWeekend {

    private Holiday holiday;          // The public holiday anchoring this long weekend
    private List<String> bridgeDays;  // ISO date strings of suggested leave days
    private int totalDaysOff;         // e.g. 4 = Fri + holiday Mon + weekend
    private int leaveDaysRequired;    // Bridge days the user must take as holiday
    private String weekdayLabel;      // e.g. "Mon–Fri"
    private String rating;            // "Optimal", "Good", "Fair"

    public LongWeekend() {}

    public LongWeekend(Holiday holiday, List<String> bridgeDays,
                       int totalDaysOff, int leaveDaysRequired,
                       String weekdayLabel, String rating) {
        this.holiday = holiday;
        this.bridgeDays = bridgeDays;
        this.totalDaysOff = totalDaysOff;
        this.leaveDaysRequired = leaveDaysRequired;
        this.weekdayLabel = weekdayLabel;
        this.rating = rating;
    }

    public Holiday getHoliday()            { return holiday; }
    public List<String> getBridgeDays()    { return bridgeDays; }
    public int getTotalDaysOff()           { return totalDaysOff; }
    public int getLeaveDaysRequired()      { return leaveDaysRequired; }
    public String getWeekdayLabel()        { return weekdayLabel; }
    public String getRating()              { return rating; }

    public void setHoliday(Holiday h)             { this.holiday = h; }
    public void setBridgeDays(List<String> days)  { this.bridgeDays = days; }
    public void setTotalDaysOff(int days)         { this.totalDaysOff = days; }
    public void setLeaveDaysRequired(int days)    { this.leaveDaysRequired = days; }
    public void setWeekdayLabel(String label)     { this.weekdayLabel = label; }
    public void setRating(String rating)          { this.rating = rating; }
}
