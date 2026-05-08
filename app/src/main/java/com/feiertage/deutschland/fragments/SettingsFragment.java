package com.feiertage.deutschland.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.activities.ThemeHelper;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.NotificationHelper;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

import java.util.List;

/**
 * SettingsFragment – embedded in the bottom-nav tab.
 * Controls dark mode, notifications, notification lead-time, and shows app info.
 */
public class SettingsFragment extends Fragment {

    private PreferenceManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferenceManager.getInstance(requireContext());

        setupDarkModeSwitch(view);
        setupNotificationSwitch(view);
        setupNotifDaysSlider(view);
        setupAppInfo(view);
    }

    private void setupDarkModeSwitch(View v) {
        MaterialSwitch sw = v.findViewById(R.id.switch_dark_mode);
        sw.setChecked(prefs.isDarkMode());
        sw.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setDarkMode(checked);
            ThemeHelper.applyTheme(checked);
            // Recreate host activity to apply theme
            if (getActivity() != null) getActivity().recreate();
        });
    }

    private void setupNotificationSwitch(View v) {
        MaterialSwitch sw = v.findViewById(R.id.switch_notifications);
        sw.setChecked(prefs.isNotificationsEnabled());
        sw.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setNotificationsEnabled(checked);
            int year      = prefs.getSelectedYear();
            String state  = prefs.getSelectedState();
            HolidayRepository repo = HolidayRepository.getInstance(requireContext());

            if (checked) {
                NotificationHelper.scheduleAllNotifications(
                        requireContext(),
                        repo.getHolidaysForState(year, state),
                        prefs.getNotifDaysBefore());
            } else {
                NotificationHelper.cancelAllNotifications(
                        requireContext(),
                        repo.getHolidaysForState(year, state));
            }
        });
    }

    private void setupNotifDaysSlider(View v) {
        Slider slider = v.findViewById(R.id.slider_notif_days);
        TextView label = v.findViewById(R.id.tv_notif_days_label);

        slider.setValue(prefs.getNotifDaysBefore());
        label.setText(prefs.getNotifDaysBefore() + " Tag(e) vorher");

        slider.addOnChangeListener((s, value, fromUser) -> {
            int days = (int) value;
            prefs.setNotifDaysBefore(days);
            label.setText(days + " Tag(e) vorher");
        });
    }

    private void setupAppInfo(View v) {
        TextView tvVersion = v.findViewById(R.id.tv_app_version);
        try {
            String version = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
            tvVersion.setText("Version " + version);
        } catch (Exception e) {
            tvVersion.setText("Version 1.0");
        }

        TextView tvYear = v.findViewById(R.id.tv_data_year);
        tvYear.setText("Datenstand: " + DateUtils.getCurrentYear()
                + " – " + (DateUtils.getCurrentYear() + 2));
    }
}
