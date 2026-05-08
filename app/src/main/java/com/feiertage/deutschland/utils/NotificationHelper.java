package com.feiertage.deutschland.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.receivers.NotificationReceiver;

import java.util.Calendar;
import java.util.List;

/**
 * NotificationHelper – schedules and cancels AlarmManager-based holiday reminders.
 * Creates the notification channel on Android O+.
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    // Notification channel constants
    public static final String CHANNEL_ID   = "feiertage_reminders";
    public static final String CHANNEL_NAME = "Feiertag Erinnerungen";
    public static final String CHANNEL_DESC = "Erinnerungen vor deutschen Feiertagen";

    // Intent extras
    public static final String EXTRA_HOLIDAY_NAME = "holiday_name";
    public static final String EXTRA_HOLIDAY_DATE = "holiday_date";
    public static final String EXTRA_HOLIDAY_ID   = "holiday_id";

    private NotificationHelper() {}

    /**
     * Creates the notification channel. Must be called before posting any notification.
     * Safe to call repeatedly (no-op if already created).
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedules alarm-based notifications for all upcoming holidays.
     *
     * @param context      application context
     * @param holidays     list of holidays to schedule
     * @param daysBefore   how many days before the holiday to fire the notification
     */
    public static void scheduleAllNotifications(Context context,
                                                List<Holiday> holidays,
                                                int daysBefore) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        String today = DateUtils.getTodayIso();

        for (Holiday holiday : holidays) {
            // Only schedule future holidays
            if (holiday.getDate().compareTo(today) < 0) continue;

            // Calculate trigger time = holiday date - daysBefore at 9:00 AM
            Calendar triggerTime = DateUtils.toCalendar(holiday.getDate());
            triggerTime.add(Calendar.DAY_OF_YEAR, -daysBefore);
            triggerTime.set(Calendar.HOUR_OF_DAY, 9);
            triggerTime.set(Calendar.MINUTE, 0);
            triggerTime.set(Calendar.SECOND, 0);
            triggerTime.set(Calendar.MILLISECOND, 0);

            // Don't schedule if trigger is in the past
            if (triggerTime.getTimeInMillis() < System.currentTimeMillis()) continue;

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra(EXTRA_HOLIDAY_NAME, holiday.getName());
            intent.putExtra(EXTRA_HOLIDAY_DATE, holiday.getDate());
            intent.putExtra(EXTRA_HOLIDAY_ID,   holiday.getId());

            // Use holiday id hashCode as unique request code
            int requestCode = holiday.getId().hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                        && alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime.getTimeInMillis(),
                            pendingIntent
                    );
                }
                Log.d(TAG, "Scheduled notification for: " + holiday.getName()
                        + " at " + triggerTime.getTime());
            } catch (SecurityException e) {
                Log.e(TAG, "Cannot schedule exact alarm: " + e.getMessage());
            }
        }

        // Record the schedule timestamp
        PreferenceManager.getInstance(context)
                .setLastNotifSchedule(System.currentTimeMillis());
    }

    /**
     * Cancels a single scheduled notification by holiday ID.
     */
    public static void cancelNotification(Context context, String holidayId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        int requestCode = holidayId.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Cancels all scheduled holiday notifications (using the same list used to schedule).
     */
    public static void cancelAllNotifications(Context context, List<Holiday> holidays) {
        for (Holiday h : holidays) {
            cancelNotification(context, h.getId());
        }
    }
}
