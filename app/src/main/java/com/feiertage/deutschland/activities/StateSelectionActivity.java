package com.feiertage.deutschland.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.adapters.StateAdapter;
import com.feiertage.deutschland.models.GermanState;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.feiertage.deutschland.utils.StateManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * StateSelectionActivity – lets the user pick which Bundesland to filter holidays by.
 * Includes an "Alle Bundesländer" option and a search bar.
 */
public class StateSelectionActivity extends AppCompatActivity {

    public static final String RESULT_STATE_CODE = "result_state_code";

    private StateAdapter adapter;
    private PreferenceManager prefs;
    private List<GermanState> allStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_selection);

        prefs = PreferenceManager.getInstance(this);

        setupToolbar();
        setupAllChip();
        setupRecyclerView();
        setupSearch();
    }

    private void setupToolbar() {
        TextView tvTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle.setText("Bundesland wählen");
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupAllChip() {
        Chip chipAll = findViewById(R.id.chip_all_states);
        chipAll.setChecked("ALL".equals(prefs.getSelectedState()));
        chipAll.setOnClickListener(v -> {
            prefs.setSelectedState("ALL");
            deliverResult("ALL");
        });
    }

    private void setupRecyclerView() {
        allStates = StateManager.getAllStates();
        adapter   = new StateAdapter(this, prefs.getSelectedState());
        adapter.setStates(allStates);
        adapter.setOnStateClickListener(state -> {
            prefs.setSelectedState(state.getCode());
            deliverResult(state.getCode());
        });

        RecyclerView rv = findViewById(R.id.rv_states);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.et_state_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int cnt, int aft) {}
            @Override public void onTextChanged(CharSequence s, int st, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterStates(s.toString().trim());
            }
        });
    }

    private void filterStates(String query) {
        if (query.isEmpty()) {
            adapter.setStates(allStates);
            return;
        }
        String lower = query.toLowerCase();
        List<GermanState> filtered = new ArrayList<>();
        for (GermanState s : allStates) {
            if (s.getName().toLowerCase().contains(lower)
                    || s.getCode().toLowerCase().contains(lower)) {
                filtered.add(s);
            }
        }
        adapter.setStates(filtered);
    }

    private void deliverResult(String stateCode) {
        Intent result = new Intent();
        result.putExtra(RESULT_STATE_CODE, stateCode);
        setResult(RESULT_OK, result);
        finish();
    }
}
