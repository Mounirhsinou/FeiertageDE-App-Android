package com.feiertage.deutschland.utils;

import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.models.LongWeekend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * LongWeekendDetector – analyses a holiday list and identifies
 * bridge-day opportunities (Brückentage) and long-weekend clusters.
 */
public class LongWeekendDetector {

    private LongWeekendDetector() {}

    /**
     * Given a list of holidays for a year/state, returns all detected
     * long-weekend opportunities sorted by total days off (descending).
     */
    public static List<LongWeekend> detect(List<Holiday> holidays) {
        List<LongWeekend> results = new ArrayList<>();
        String today = DateUtils.getTodayIso();

        for (Holiday holiday : holidays) {
            // Skip past holidays
            if (holiday.getDate().compareTo(today) < 0) continue;

            int dow = DateUtils.getDayOfWeek(holiday.getDate());
            List<String> bridgeDays = new ArrayList<>();
            int totalDays;
            int leaveDays;
            String label;
            String rating;

            if (dow == Calendar.TUESDAY) {
                // Take Monday off → Mon-Tue holiday = 4-day weekend (Sat-Tue)
                String monday = getAdjacentDay(holiday.getDate(), -1);
                bridgeDays.add(monday);
                totalDays = 4;
                leaveDays = 1;
                label = "Sa–Di";
                rating = "Optimal";

            } else if (dow == Calendar.THURSDAY) {
                // Take Friday off → Thu holiday + Fri bridge = 4-day weekend (Thu-Sun)
                String friday = getAdjacentDay(holiday.getDate(), 1);
                bridgeDays.add(friday);
                totalDays = 4;
                leaveDays = 1;
                label = "Do–So";
                rating = "Optimal";

            } else if (dow == Calendar.WEDNESDAY) {
                // Need Mon+Tue or Thu+Fri → 5 days but costs 2 bridge days
                String thursday = getAdjacentDay(holiday.getDate(), 1);
                String friday   = getAdjacentDay(holiday.getDate(), 2);
                bridgeDays.add(thursday);
                bridgeDays.add(friday);
                totalDays = 5;
                leaveDays = 2;
                label = "Mi–So";
                rating = "Good";

            } else if (dow == Calendar.MONDAY) {
                // Already attached to weekend — standard 3-day weekend
                totalDays = 3;
                leaveDays = 0;
                label = "Sa–Mo";
                rating = "Good";

            } else if (dow == Calendar.FRIDAY) {
                // Already attached to weekend — standard 3-day weekend
                totalDays = 3;
                leaveDays = 0;
                label = "Fr–So";
                rating = "Good";

            } else {
                // Sat or Sun — holiday falls on weekend (less valuable)
                totalDays = 1;
                leaveDays = 0;
                label = "Wochenende";
                rating = "Fair";
            }

            results.add(new LongWeekend(holiday, bridgeDays, totalDays, leaveDays, label, rating));
        }

        // Sort: Optimal first, then by total days descending
        results.sort((a, b) -> {
            int ra = ratingScore(a.getRating());
            int rb = ratingScore(b.getRating());
            if (ra != rb) return rb - ra;
            return b.getTotalDaysOff() - a.getTotalDaysOff();
        });

        return results;
    }

    /** Returns the best single long-weekend opportunity. */
    public static LongWeekend getBest(List<Holiday> holidays) {
        List<LongWeekend> all = detect(holidays);
        return all.isEmpty() ? null : all.get(0);
    }

    /** Shift an ISO date by +/- days. */
    private static String getAdjacentDay(String isoDate, int deltaDays) {
        Calendar cal = DateUtils.toCalendar(isoDate);
        cal.add(Calendar.DAY_OF_YEAR, deltaDays);
        return DateUtils.toIso(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
        );
    }

    private static int ratingScore(String rating) {
        switch (rating) {
            case "Optimal": return 3;
            case "Good":    return 2;
            case "Fair":    return 1;
            default:        return 0;
        }
    }
}
