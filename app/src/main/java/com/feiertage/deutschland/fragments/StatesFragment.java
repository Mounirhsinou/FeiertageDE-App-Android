package com.feiertage.deutschland.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.feiertage.deutschland.adapters.HolidayAdapter;
import com.feiertage.deutschland.adapters.StateAdapter;
import com.feiertage.deutschland.models.GermanState;
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.HolidayRepository;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.feiertage.deutschland.utils.StateManager;

import java.util.List;

/**
 * StatesFragment – shows all 16 Bundesländer.
 * Tapping a state previews that state's holidays in an inline RecyclerView.
 */
public class StatesFragment extends Fragment {

    private PreferenceManager prefs;
    private HolidayRepository repo;
    private StateAdapter stateAdapter;
    private HolidayAdapter holidayAdapter;
    private TextView tvSelectedStateName;
    private TextView tvHolidayCount;
    private String currentPreviewState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_states, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs              = PreferenceManager.getInstance(requireContext());
        repo               = HolidayRepository.getInstance(requireContext());
        currentPreviewState = prefs.getSelectedState();

        tvSelectedStateName = view.findViewById(R.id.tv_preview_state_name);
        tvHolidayCount      = view.findViewById(R.id.tv_preview_holiday_count);

        setupStateList(view);
        setupHolidayPreview(view);
        loadPreview(currentPreviewState);
    }

    private void setupStateList(View v) {
        stateAdapter = new StateAdapter(requireContext(), prefs.getSelectedState());
        stateAdapter.setStates(StateManager.getAllStates());

        stateAdapter.setOnStateClickListener(state -> {
            prefs.setSelectedState(state.getCode());
            stateAdapter.setSelectedCode(state.getCode());
            currentPreviewState = state.getCode();
            loadPreview(state.getCode());
        });

        RecyclerView rvStates = v.findViewById(R.id.rv_states_list);
        rvStates.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvStates.setAdapter(stateAdapter);
    }

    private void setupHolidayPreview(View v) {
        holidayAdapter = new HolidayAdapter(requireContext());
        holidayAdapter.setOnHolidayClickListener((holiday, pos) -> {
            Intent intent = new Intent(requireContext(), HolidayDetailActivity.class);
            intent.putExtra(HolidayDetailActivity.EXTRA_HOLIDAY, holiday);
            startActivity(intent);
        });
        holidayAdapter.setOnFavoriteClickListener((holiday, pos) -> {
            prefs.toggleFavorite(holiday.getId());
            holidayAdapter.updateFavoriteAt(pos);
        });

        RecyclerView rvPreview = v.findViewById(R.id.rv_state_holidays_preview);
        rvPreview.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPreview.setAdapter(holidayAdapter);
        rvPreview.setNestedScrollingEnabled(false);
    }

    private void loadPreview(String stateCode) {
        int year = prefs.getSelectedYear();
        List<Holiday> holidays = repo.getHolidaysForState(year, stateCode);

        GermanState state = StateManager.getByCode(stateCode);
        if (state != null) {
            tvSelectedStateName.setText(state.getName());
        } else {
            tvSelectedStateName.setText("Alle Bundesländer");
        }
        tvHolidayCount.setText(holidays.size() + " Feiertage " + year);
        holidayAdapter.setHolidays(holidays);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreview(prefs.getSelectedState());
        stateAdapter.setSelectedCode(prefs.getSelectedState());
    }
}
