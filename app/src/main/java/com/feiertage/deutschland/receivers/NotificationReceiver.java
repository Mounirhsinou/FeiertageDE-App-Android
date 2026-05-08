package com.feiertage.deutschland.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.activities.MainActivity;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.NotificationHelper;

/**
 * NotificationReceiver – fired by AlarmManager to post a holiday reminder notification.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String holidayName = intent.getStringExtra(NotificationHelper.EXTRA_HOLIDAY_NAME);
        String holidayDate = intent.getStringExtra(NotificationHelper.EXTRA_HOLIDAY_DATE);

        if (holidayName == null || holidayDate == null) return;

        // Ensure notification channel exists
        NotificationHelper.createNotificationChannel(context);

        // Tap action → open MainActivity
        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String displayDate = DateUtils.formatIsoToDisplay(holidayDate);
        long daysUntil     = DateUtils.getDaysUntil(holidayDate);

        String contentText;
        if (daysUntil == 0) {
            contentText = "Heute ist " + holidayName + "!";
        } else if (daysUntil == 1) {
            contentText = "Morgen: " + holidayName + " am " + displayDate;
        } else {
            contentText = "In " + daysUntil + " Tagen: " + holidayName + " am " + displayDate;
        }

        Notification notification = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("🇩🇪 Feiertag Erinnerung")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(0xFFA8000A)
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            // Use holiday name hashCode as notification ID for uniqueness
            manager.notify(holidayName.hashCode(), notification);
        }
    }
}
