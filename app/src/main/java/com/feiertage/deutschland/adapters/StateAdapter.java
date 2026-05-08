package com.feiertage.deutschland.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.models.GermanState;

import java.util.ArrayList;
import java.util.List;

/**
 * StateAdapter – RecyclerView adapter for the state selection screen.
 * Highlights the currently selected state.
 */
public class StateAdapter extends RecyclerView.Adapter<StateAdapter.StateViewHolder> {

    public interface OnStateClickListener {
        void onStateClick(GermanState state);
    }

    private final Context context;
    private final List<GermanState> states;
    private String selectedCode;
    private OnStateClickListener clickListener;

    public StateAdapter(Context context, String selectedCode) {
        this.context      = context;
        this.states       = new ArrayList<>();
        this.selectedCode = selectedCode != null ? selectedCode : "ALL";
    }

    public void setStates(List<GermanState> list) {
        states.clear();
        if (list != null) states.addAll(list);
        notifyDataSetChanged();
    }

    public void setSelectedCode(String code) {
        this.selectedCode = code;
        notifyDataSetChanged();
    }

    public void setOnStateClickListener(OnStateClickListener l) {
        this.clickListener = l;
    }

    @NonNull
    @Override
    public StateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_state, parent, false);
        return new StateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StateViewHolder holder, int position) {
        GermanState state = states.get(position);
        boolean isSelected = state.getCode().equals(selectedCode);
        holder.bind(state, isSelected);
    }

    @Override
    public int getItemCount() { return states.size(); }

    class StateViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView tvCode;
        TextView tvName;
        TextView tvCapital;
        View ivCheck;

        StateViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_state);
            tvCode    = itemView.findViewById(R.id.tv_state_code);
            tvName    = itemView.findViewById(R.id.tv_state_name);
            tvCapital = itemView.findViewById(R.id.tv_state_capital);
            ivCheck   = itemView.findViewById(R.id.iv_state_check);
        }

        void bind(GermanState state, boolean selected) {
            tvCode.setText(state.getCode());
            tvName.setText(state.getName());
            tvCapital.setText(state.getCapital());
            ivCheck.setVisibility(selected ? View.VISIBLE : View.GONE);

            if (selected) {
                container.setBackgroundResource(R.drawable.bg_state_selected);
                tvName.setTextColor(ContextCompat.getColor(context, R.color.primary));
                tvCode.setTextColor(ContextCompat.getColor(context, R.color.on_primary));
            } else {
                container.setBackgroundResource(R.drawable.bg_state_normal);
                tvName.setTextColor(ContextCompat.getColor(context, R.color.on_surface));
                tvCode.setTextColor(ContextCompat.getColor(context, R.color.on_surface_variant));
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onStateClick(state);
            });
        }
    }
}
