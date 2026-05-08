package com.feiertage.deutschland.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Static utility methods for date formatting, parsing, and arithmetic.
 */
public class DateUtils {

    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("d. MMMM yyyy", Locale.GERMANY);
    private static final SimpleDateFormat MONTH_YEAR_FORMAT =
            new SimpleDateFormat("MMMM yyyy", Locale.GERMANY);
    private static final SimpleDateFormat SHORT_DATE_FORMAT =
            new SimpleDateFormat("d. MMM", Locale.GERMANY);
    private static final SimpleDateFormat MONTH_SHORT_FORMAT =
            new SimpleDateFormat("MMM", Locale.GERMANY);
    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("d", Locale.GERMANY);

    private DateUtils() {} // Utility class, no instances

    /** Returns today's date in ISO 8601 format: "YYYY-MM-DD". */
    public static String getTodayIso() {
        return ISO_FORMAT.format(new Date());
    }

    /** Returns the current calendar year as an int. */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /** Returns the current month (1-based). */
    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * Parses an ISO date string ("YYYY-MM-DD") into a Date object.
     * Returns null on parse error.
     */
    public static Date parseIso(String isoDate) {
        try {
            return ISO_FORMAT.parse(isoDate);
        } catch (ParseException e) {
            return null;
        }
    }

    /** Formats a Date into "d. MMMM yyyy" (e.g., "1. Januar 2026"). */
    public static String formatDisplay(Date date) {
        if (date == null) return "";
        return DISPLAY_FORMAT.format(date);
    }

    /** Formats an ISO string into a display string. */
    public static String formatIsoToDisplay(String isoDate) {
        Date d = parseIso(isoDate);
        return d != null ? formatDisplay(d) : isoDate;
    }

    /** Returns the German weekday name for an ISO date string. */
    public static String getWeekdayName(String isoDate) {
        Date d = parseIso(isoDate);
        if (d == null) return "";
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EEEE", Locale.GERMANY);
        return dayOfWeek.format(d);
    }

    /** Returns the abbreviated German weekday (e.g. "Mo", "Di"). */
    public static String getWeekdayShort(String isoDate) {
        Date d = parseIso(isoDate);
        if (d == null) return "";
        SimpleDateFormat dayOfWeek = new SimpleDateFormat("EE", Locale.GERMANY);
        return dayOfWeek.format(d);
    }

    /** Returns the month abbreviation (e.g. "Jan", "Feb") in German. */
    public static String getMonthAbbrev(String isoDate) {
        Date d = parseIso(isoDate);
        if (d == null) return "";
        return MONTH_SHORT_FORMAT.format(d).toUpperCase(Locale.GERMANY);
    }

    /** Returns just the day-of-month as a string (e.g. "25"). */
    public static String getDayOfMonth(String isoDate) {
        Date d = parseIso(isoDate);
        if (d == null) return "";
        return DAY_FORMAT.format(d);
    }

    /**
     * Calculates the number of days remaining from today until the given ISO date.
     * Returns 0 if today, negative if past.
     */
    public static long getDaysUntil(String isoDate) {
        Date target = parseIso(isoDate);
        Date today = parseIso(getTodayIso());
        if (target == null || today == null) return Long.MAX_VALUE;
        long diff = target.getTime() - today.getTime();
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    /**
     * Returns a human-readable countdown string.
     * e.g. "Heute", "In 3 Tagen", "In 1 Tag", "Vor 2 Tagen"
     */
    public static String getCountdownLabel(String isoDate) {
        long days = getDaysUntil(isoDate);
        if (days == 0) return "Heute";
        if (days == 1) return "Morgen";
        if (days == -1) return "Gestern";
        if (days > 1)  return "In " + days + " Tagen";
        return "Vor " + Math.abs(days) + " Tagen";
    }

    /**
     * Returns the day-of-week index for an ISO date.
     * Calendar.MONDAY = 2 … Calendar.SUNDAY = 1
     */
    public static int getDayOfWeek(String isoDate) {
        Date d = parseIso(isoDate);
        if (d == null) return -1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /** Returns true if the ISO date falls on a weekend (Sat or Sun). */
    public static boolean isWeekend(String isoDate) {
        int dow = getDayOfWeek(isoDate);
        return dow == Calendar.SATURDAY || dow == Calendar.SUNDAY;
    }

    /**
     * Returns a Calendar object for the given ISO date at midnight.
     */
    public static Calendar toCalendar(String isoDate) {
        Date d = parseIso(isoDate);
        Calendar cal = Calendar.getInstance();
        if (d != null) cal.setTime(d);
        return cal;
    }

    /** Returns "MMMM yyyy" display (e.g. "Januar 2026"). */
    public static String formatMonthYear(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return MONTH_YEAR_FORMAT.format(cal.getTime());
    }

    /** Returns number of days in a given month/year. */
    public static int getDaysInMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /** Returns the ISO string for a given year/month/day. */
    public static String toIso(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
    }
}
