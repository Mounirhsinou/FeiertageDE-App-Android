package com.feiertage.deutschland.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.activities.HolidayDetailActivity;
import com.feiertage.deutschland.adapters.CalendarDayAdapter;
import com.feiertage.deutschland.adapters.HolidayAdapter;
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * CalendarFragment – displays a monthly grid calendar with holiday dot indicators.
 * Tapping a day with holidays shows a BottomSheet with the holiday list.
 */
public class CalendarFragment extends Fragment {

    private PreferenceManager prefs;
    private HolidayRepository repo;

    private TextView tvMonthYear;
    private CalendarDayAdapter dayAdapter;
    private MaterialButton btnPrevMonth;
    private MaterialButton btnNextMonth;

    private int displayYear;
    private int displayMonth; // 1-based

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs        = PreferenceManager.getInstance(requireContext());
        repo         = HolidayRepository.getInstance(requireContext());
        displayYear  = DateUtils.getCurrentYear();
        displayMonth = DateUtils.getCurrentMonth();

        tvMonthYear   = view.findViewById(R.id.tv_month_year);
        btnPrevMonth  = view.findViewById(R.id.btn_prev_month);
        btnNextMonth  = view.findViewById(R.id.btn_next_month);

        setupWeekdayHeaders(view);
        setupGrid(view);
        setupNavButtons();
        renderMonth();
    }

    // ─── Setup ───────────────────────────────────────────────────────────────

    private void setupWeekdayHeaders(View v) {
        // Day headers: Mo Di Mi Do Fr Sa So
        String[] days = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        int[] ids = {
                R.id.tv_cal_mon, R.id.tv_cal_tue, R.id.tv_cal_wed,
                R.id.tv_cal_thu, R.id.tv_cal_fri, R.id.tv_cal_sat, R.id.tv_cal_sun
        };
        for (int i = 0; i < ids.length; i++) {
            TextView tv = v.findViewById(ids[i]);
            if (tv != null) tv.setText(days[i]);
        }
    }

    private void setupGrid(View v) {
        RecyclerView rvGrid = v.findViewById(R.id.rv_calendar_grid);
        dayAdapter = new CalendarDayAdapter(requireContext());
        rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        rvGrid.setAdapter(dayAdapter);
        rvGrid.setItemAnimator(null);

        dayAdapter.setOnDayClickListener((year, month, day, holidays) ->
                showHolidayBottomSheet(holidays));
    }

    private void setupNavButtons() {
        btnPrevMonth.setOnClickListener(v -> {
            displayMonth--;
            if (displayMonth < 1) { displayMonth = 12; displayYear--; }
            renderMonth();
        });
        btnNextMonth.setOnClickListener(v -> {
            displayMonth++;
            if (displayMonth > 12) { displayMonth = 1; displayYear++; }
            renderMonth();
        });
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    private void renderMonth() {
        tvMonthYear.setText(DateUtils.formatMonthYear(displayYear, displayMonth));

        String stateCode  = prefs.getSelectedState();
        List<Holiday> monthHolidays =
                repo.getHolidaysForMonth(displayYear, displayMonth, stateCode);

        // Build a map: day → holidays
        java.util.Map<Integer, List<Holiday>> dayMap = new java.util.HashMap<>();
        for (Holiday h : monthHolidays) {
            int day = Integer.parseInt(DateUtils.getDayOfMonth(h.getDate()));
            dayMap.computeIfAbsent(day, k -> new ArrayList<>()).add(h);
        }

        String today = DateUtils.getTodayIso();
        int todayYear  = DateUtils.getCurrentYear();
        int todayMonth = DateUtils.getCurrentMonth();
        int todayDay   = Integer.parseInt(DateUtils.getDayOfMonth(today));

        // Compute first-day offset (Monday-based grid)
        Calendar firstDay = Calendar.getInstance();
        firstDay.set(displayYear, displayMonth - 1, 1);
        int firstDow = firstDay.get(Calendar.DAY_OF_WEEK);
        // Convert Sunday=1 to Monday-based: Mon=0..Sun=6
        int offset = (firstDow == Calendar.SUNDAY) ? 6 : firstDow - Calendar.MONDAY;

        int daysInMonth = DateUtils.getDaysInMonth(displayYear, displayMonth);
        List<CalendarDayAdapter.DayCell> cells = new ArrayList<>();

        // Padding cells
        for (int i = 0; i < offset; i++) {
            CalendarDayAdapter.DayCell empty = new CalendarDayAdapter.DayCell();
            cells.add(empty);
        }

        // Day cells
        for (int d = 1; d <= daysInMonth; d++) {
            CalendarDayAdapter.DayCell cell = new CalendarDayAdapter.DayCell();
            cell.day   = d;
            cell.year  = displayYear;
            cell.month = displayMonth;
            cell.isToday = (d == todayDay
                    && displayMonth == todayMonth
                    && displayYear  == todayYear);

            // Determine weekend
            Calendar c = Calendar.getInstance();
            c.set(displayYear, displayMonth - 1, d);
            int dow = c.get(Calendar.DAY_OF_WEEK);
            cell.isWeekend = (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY);

            cell.holidays = dayMap.getOrDefault(d, new ArrayList<>());
            cells.add(cell);
        }

        dayAdapter.setCells(cells);
    }

    // ─── Bottom Sheet ────────────────────────────────────────────────────────

    private void showHolidayBottomSheet(List<Holiday> holidays) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(),
                R.style.Theme_FeiertageDE_BottomSheet);
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_holidays, null);

        RecyclerView rv = sheetView.findViewById(R.id.rv_sheet_holidays);
        HolidayAdapter sheetAdapter = new HolidayAdapter(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(sheetAdapter);
        sheetAdapter.setHolidays(holidays);

        sheetAdapter.setOnHolidayClickListener((holiday, pos) -> {
            dialog.dismiss();
            Intent intent = new Intent(requireContext(), HolidayDetailActivity.class);
            intent.putExtra(HolidayDetailActivity.EXTRA_HOLIDAY, holiday);
            startActivity(intent);
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderMonth(); // Refresh in case state changed
    }
}
