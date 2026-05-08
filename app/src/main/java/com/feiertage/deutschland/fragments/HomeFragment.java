package com.feiertage.deutschland.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.activities.HolidayDetailActivity;
import com.feiertage.deutschland.activities.StateSelectionActivity;
import com.feiertage.deutschland.adapters.HolidayAdapter;
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.models.LongWeekend;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.LongWeekendDetector;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.feiertage.deutschland.utils.StateManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * HomeFragment – main landing screen.
 *
 * Displays:
 *  • Year selector chips
 *  • Search bar
 *  • "Next Holiday" hero card
 *  • "Long Weekend" summary card
 *  • Upcoming holidays RecyclerView
 */
public class HomeFragment extends Fragment {

    private PreferenceManager prefs;
    private HolidayRepository repo;
    private HolidayAdapter adapter;

    private TextView tvNextHolidayName;
    private TextView tvNextHolidayDate;
    private TextView tvNextHolidayWeekday;
    private TextView tvNextCountdown;
    private TextView tvStateLabel;
    private TextView tvBridgeRating;
    private TextView tvBridgeDetails;
    private TextView tvEmptyState;

    private ChipGroup chipGroupYear;
    private EditText  etSearch;
    private RecyclerView rvHolidays;

    private int selectedYear;
    private String selectedState;

    // Launcher for state picker
    private final ActivityResultLauncher<Intent> stateLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            String code = result.getData()
                                    .getStringExtra(StateSelectionActivity.RESULT_STATE_CODE);
                            if (code != null) {
                                selectedState = code;
                                prefs.setSelectedState(code);
                                refreshAll();
                            }
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs         = PreferenceManager.getInstance(requireContext());
        repo          = HolidayRepository.getInstance(requireContext());
        selectedYear  = prefs.getSelectedYear();
        selectedState = prefs.getSelectedState();

        bindViews(view);
        setupYearChips(view);
        setupSearch();
        setupRecyclerView();
        setupStateChip(view);

        refreshAll();
    }

    // ─── View Binding ────────────────────────────────────────────────────────

    private void bindViews(View v) {
        tvNextHolidayName    = v.findViewById(R.id.tv_next_holiday_name);
        tvNextHolidayDate    = v.findViewById(R.id.tv_next_holiday_date);
        tvNextHolidayWeekday = v.findViewById(R.id.tv_next_holiday_weekday);
        tvNextCountdown      = v.findViewById(R.id.tv_next_countdown);
        tvStateLabel         = v.findViewById(R.id.tv_state_label);
        tvBridgeRating       = v.findViewById(R.id.tv_bridge_rating);
        tvBridgeDetails      = v.findViewById(R.id.tv_bridge_details);
        tvEmptyState         = v.findViewById(R.id.tv_empty_state);
        chipGroupYear        = v.findViewById(R.id.chip_group_year);
        etSearch             = v.findViewById(R.id.et_search);
        rvHolidays           = v.findViewById(R.id.rv_holidays);

        // Next holiday card click → detail screen
        v.findViewById(R.id.card_next_holiday).setOnClickListener(cv -> {
            Holiday next = repo.getNextHoliday(selectedState);
            if (next != null) openDetail(next);
        });
    }

    // ─── Year Chips ──────────────────────────────────────────────────────────

    private void setupYearChips(View v) {
        int currentYear = DateUtils.getCurrentYear();
        chipGroupYear.removeAllViews();
        for (int y = currentYear - 1; y <= currentYear + 2; y++) {
            Chip chip = new Chip(requireContext());
            chip.setText(String.valueOf(y));
            chip.setCheckable(true);
            chip.setChecked(y == selectedYear);
            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
            chip.setTextColor(requireContext().getColorStateList(R.color.chip_text_selector));
            int year = y;
            chip.setOnClickListener(c -> {
                selectedYear = year;
                prefs.setSelectedYear(year);
                refreshAll();
            });
            chipGroupYear.addView(chip);
        }
    }

    // ─── Search ──────────────────────────────────────────────────────────────

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int cnt, int aft) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                loadHolidayList(s.toString().trim());
            }
        });
    }

    // ─── State Chip ──────────────────────────────────────────────────────────

    private void setupStateChip(View v) {
        Chip chipState = v.findViewById(R.id.chip_state_filter);
        chipState.setOnClickListener(c -> {
            Intent intent = new Intent(requireContext(), StateSelectionActivity.class);
            stateLauncher.launch(intent);
        });
        updateStateChip(chipState);
    }

    private void updateStateChip(Chip chip) {
        chip.setText(StateManager.getDisplayName(selectedState));
    }

    // ─── RecyclerView ────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new HolidayAdapter(requireContext());
        rvHolidays.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHolidays.setAdapter(adapter);
        rvHolidays.setItemAnimator(null); // Avoid flicker during search

        adapter.setOnHolidayClickListener((holiday, pos) -> openDetail(holiday));
        adapter.setOnFavoriteClickListener((holiday, pos) -> {
            prefs.toggleFavorite(holiday.getId());
            adapter.updateFavoriteAt(pos);
        });
    }

    // ─── Refresh ─────────────────────────────────────────────────────────────

    private void refreshAll() {
        loadNextHolidayCard();
        loadBridgeDayCard();
        loadHolidayList("");
        etSearch.setText("");
        // Update state chip text
        Chip chipState = getView() != null ? getView().findViewById(R.id.chip_state_filter) : null;
        if (chipState != null) updateStateChip(chipState);
    }

    private void loadNextHolidayCard() {
        Holiday next = repo.getNextHoliday(selectedState);
        if (next == null) return;

        tvNextHolidayName.setText(next.getName());
        tvNextHolidayDate.setText(DateUtils.formatIsoToDisplay(next.getDate()));
        tvNextHolidayWeekday.setText(DateUtils.getWeekdayName(next.getDate()));
        tvNextCountdown.setText(DateUtils.getCountdownLabel(next.getDate()));
        tvStateLabel.setText(StateManager.getDisplayName(selectedState));
    }

    private void loadBridgeDayCard() {
        List<Holiday> holidays = repo.getHolidaysForState(selectedYear, selectedState);
        LongWeekend best = LongWeekendDetector.getBest(holidays);
        if (best != null) {
            tvBridgeRating.setText(best.getRating());
            String details = best.getHoliday().getName()
                    + " · " + best.getTotalDaysOff() + " Tage";
            if (best.getLeaveDaysRequired() > 0) {
                details += " · " + best.getLeaveDaysRequired() + " Urlaub nötig";
            }
            tvBridgeDetails.setText(details);
        }
    }

    private void loadHolidayList(String query) {
        List<Holiday> list = repo.searchHolidays(selectedYear, selectedState, query);
        adapter.setHolidays(list);

        boolean empty = list.isEmpty();
        tvEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvHolidays.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void openDetail(Holiday holiday) {
        Intent intent = new Intent(requireContext(), HolidayDetailActivity.class);
        intent.putExtra(HolidayDetailActivity.EXTRA_HOLIDAY, holiday);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out);
    }
}
