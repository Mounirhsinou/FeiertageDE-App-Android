package com.feiertage.deutschland.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.feiertage.deutschland.utils.StateManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * HolidayDetailActivity – displays full information about a single holiday.
 * Passed via Intent extras (Holiday object as Serializable).
 */
public class HolidayDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HOLIDAY = "extra_holiday";

    private PreferenceManager prefs;
    private Holiday holiday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_detail);

        prefs   = PreferenceManager.getInstance(this);
        holiday = (Holiday) getIntent().getSerializableExtra(EXTRA_HOLIDAY);

        if (holiday == null) { finish(); return; }

        setupToolbar();
        bindHolidayData();
        setupFabFavorite();
    }

    private void setupToolbar() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText(holiday.getName());
    }

    private void bindHolidayData() {
        // Header date pill
        TextView tvDate = findViewById(R.id.tv_detail_date);
        tvDate.setText(DateUtils.formatIsoToDisplay(holiday.getDate()));

        // Weekday
        TextView tvWeekday = findViewById(R.id.tv_detail_weekday);
        tvWeekday.setText(DateUtils.getWeekdayName(holiday.getDate()));

        // Countdown
        TextView tvCountdown = findViewById(R.id.tv_detail_countdown);
        long days = DateUtils.getDaysUntil(holiday.getDate());
        if (days == 0) {
            tvCountdown.setText("Heute!");
            tvCountdown.setTextColor(ContextCompat.getColor(this, R.color.tertiary_container));
        } else if (days > 0) {
            tvCountdown.setText("Noch " + days + " Tag" + (days == 1 ? "" : "e"));
        } else {
            tvCountdown.setText("Vor " + Math.abs(days) + " Tag" + (Math.abs(days) == 1 ? "" : "en"));
        }

        // Holiday name
        TextView tvName = findViewById(R.id.tv_detail_name);
        tvName.setText(holiday.getName());

        // English name
        TextView tvNameEn = findViewById(R.id.tv_detail_name_en);
        if (holiday.getNameEn() != null && !holiday.getNameEn().isEmpty()) {
            tvNameEn.setText(holiday.getNameEn());
            tvNameEn.setVisibility(android.view.View.VISIBLE);
        } else {
            tvNameEn.setVisibility(android.view.View.GONE);
        }

        // Description
        TextView tvDesc = findViewById(R.id.tv_detail_description);
        tvDesc.setText(holiday.getDescription());

        // National / regional badge
        TextView tvScope = findViewById(R.id.tv_detail_scope);
        if (holiday.isNational()) {
            tvScope.setText("Nationaler Feiertag – gilt in allen 16 Bundesländern");
            tvScope.setBackgroundResource(R.drawable.bg_badge_national);
            tvScope.setTextColor(ContextCompat.getColor(this, R.color.on_primary));
        } else {
            int count = holiday.getStates() != null ? holiday.getStates().size() : 0;
            tvScope.setText("Landesfeiertag – gilt in " + count + " Bundesland/Bundesländern");
            tvScope.setBackgroundResource(R.drawable.bg_badge_state);
            tvScope.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        }

        // State chips
        if (!holiday.isNational() && holiday.getStates() != null) {
            com.google.android.material.chip.ChipGroup chipGroup =
                    findViewById(R.id.chipgroup_states);
            chipGroup.setVisibility(android.view.View.VISIBLE);
            for (String code : holiday.getStates()) {
                Chip chip = new Chip(this);
                chip.setText(StateManager.getDisplayName(code));
                chip.setChipBackgroundColorResource(R.color.surface_container);
                chip.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
                chip.setClickable(false);
                chipGroup.addView(chip);
            }
        }
    }

    private void setupFabFavorite() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_favorite);
        updateFabState(fab);

        fab.setOnClickListener(v -> {
            prefs.toggleFavorite(holiday.getId());
            holiday.setFavorite(prefs.isFavorite(holiday.getId()));
            updateFabState(fab);
            Toast.makeText(this,
                    holiday.isFavorite()
                            ? "Zu Favoriten hinzugefügt"
                            : "Aus Favoriten entfernt",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFabState(ExtendedFloatingActionButton fab) {
        boolean fav = prefs.isFavorite(holiday.getId());
        fab.setIconResource(fav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
        fab.setText(fav ? "Favorit" : "Speichern");
        fab.setIconTintResource(fav ? R.color.on_primary : R.color.on_primary);
    }
}
