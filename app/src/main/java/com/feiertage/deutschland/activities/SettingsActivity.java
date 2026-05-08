package com.feiertage.deutschland.activities;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.NotificationHelper;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

/**
 * SettingsActivity – standalone settings screen (also used inside SettingsFragment).
 * Handles dark mode, notifications, notification advance days.
 */
public class SettingsActivity extends AppCompatActivity {

    private PreferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = PreferenceManager.getInstance(this);
        setupToolbar();
        setupDarkMode();
        setupNotifications();
        setupNotifDays();
        setupAppInfo();
    }

    private void setupToolbar() {
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText("Einstellungen");
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupDarkMode() {
        MaterialSwitch switchDark = findViewById(R.id.switch_dark_mode);
        switchDark.setChecked(prefs.isDarkMode());
        switchDark.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setDarkMode(checked);
            ThemeHelper.applyTheme(checked);
            recreate();
        });
    }

    private void setupNotifications() {
        MaterialSwitch switchNotif = findViewById(R.id.switch_notifications);
        switchNotif.setChecked(prefs.isNotificationsEnabled());
        switchNotif.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setNotificationsEnabled(checked);
            if (!checked) {
                // Cancel all pending notifications
                HolidayRepository repo = HolidayRepository.getInstance(this);
                int year = com.feiertage.deutschland.utils.DateUtils.getCurrentYear();
                String state = prefs.getSelectedState();
                NotificationHelper.cancelAllNotifications(this,
                        repo.getHolidaysForState(year, state));
            } else {
                // Reschedule
                HolidayRepository repo = HolidayRepository.getInstance(this);
                int year = com.feiertage.deutschland.utils.DateUtils.getCurrentYear();
                String state = prefs.getSelectedState();
                NotificationHelper.scheduleAllNotifications(this,
                        repo.getHolidaysForState(year, state),
                        prefs.getNotifDaysBefore());
            }
        });
    }

    private void setupNotifDays() {
        Slider sliderDays = findViewById(R.id.slider_notif_days);
        TextView tvDaysLabel = findViewById(R.id.tv_notif_days_label);
        sliderDays.setValue(prefs.getNotifDaysBefore());
        tvDaysLabel.setText(prefs.getNotifDaysBefore() + " Tag(e) vorher");

        sliderDays.addOnChangeListener((slider, value, fromUser) -> {
            int days = (int) value;
            prefs.setNotifDaysBefore(days);
            tvDaysLabel.setText(days + " Tag(e) vorher");
        });
    }

    private void setupAppInfo() {
        TextView tvVersion = findViewById(R.id.tv_app_version);
        try {
            String version = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText("Version " + version);
        } catch (Exception e) {
            tvVersion.setText("Version 1.0");
        }
    }
}
