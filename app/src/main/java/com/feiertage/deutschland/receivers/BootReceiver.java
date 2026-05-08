package com.feiertage.deutschland.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.NotificationHelper;
import com.feiertage.deutschland.utils.PreferenceManager;

import java.util.List;

/**
 * BootReceiver – re-schedules holiday notifications after device reboot,
 * since AlarmManager alarms do not survive a reboot.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !"android.intent.action.QUICKBOOT_POWERON".equals(action)) {
            return;
        }

        Log.d(TAG, "Boot completed – rescheduling holiday notifications");

        PreferenceManager prefs = PreferenceManager.getInstance(context);
        if (!prefs.isNotificationsEnabled()) {
            Log.d(TAG, "Notifications disabled – skipping reschedule");
            return;
        }

        int year          = DateUtils.getCurrentYear();
        String stateCode  = prefs.getSelectedState();
        int daysBefore    = prefs.getNotifDaysBefore();

        HolidayRepository repo = HolidayRepository.getInstance(context);

        // Schedule for current year
        List<Holiday> thisYear = repo.getHolidaysForState(year, stateCode);
        NotificationHelper.scheduleAllNotifications(context, thisYear, daysBefore);

        // Also schedule for next year in case we're in December
        List<Holiday> nextYear = repo.getHolidaysForState(year + 1, stateCode);
        NotificationHelper.scheduleAllNotifications(context, nextYear, daysBefore);

        Log.d(TAG, "Rescheduled " + (thisYear.size() + nextYear.size()) + " notifications");
    }
}
