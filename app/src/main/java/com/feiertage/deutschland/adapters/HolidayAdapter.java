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
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;
import com.feiertage.deutschland.utils.PreferenceManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * HolidayAdapter – RecyclerView adapter for the main holiday list.
 * Supports click, long-click, and favorite toggle callbacks.
 */
public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder> {

    public interface OnHolidayClickListener {
        void onHolidayClick(Holiday holiday, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Holiday holiday, int position);
    }

    private final Context context;
    private final List<Holiday> holidays;
    private final PreferenceManager prefs;

    private OnHolidayClickListener clickListener;
    private OnFavoriteClickListener favoriteListener;

    public HolidayAdapter(Context context) {
        this.context  = context;
        this.holidays = new ArrayList<>();
        this.prefs    = PreferenceManager.getInstance(context);
    }

    public void setOnHolidayClickListener(OnHolidayClickListener l)   { clickListener    = l; }
    public void setOnFavoriteClickListener(OnFavoriteClickListener l)  { favoriteListener = l; }

    // ─── Data operations ────────────────────────────────────────────────────

    public void setHolidays(List<Holiday> newList) {
        holidays.clear();
        if (newList != null) {
            for (Holiday h : newList) {
                h.setFavorite(prefs.isFavorite(h.getId()));
                holidays.add(h);
            }
        }
        notifyDataSetChanged();
    }

    public void updateFavoriteAt(int position) {
        if (position >= 0 && position < holidays.size()) {
            Holiday h = holidays.get(position);
            h.setFavorite(prefs.isFavorite(h.getId()));
            notifyItemChanged(position);
        }
    }

    public Holiday getItem(int position) {
        return holidays.get(position);
    }

    // ─── RecyclerView.Adapter ───────────────────────────────────────────────

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_holiday, parent, false);
        return new HolidayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        Holiday holiday = holidays.get(position);
        holder.bind(holiday, position);
    }

    @Override
    public int getItemCount() { return holidays.size(); }

    // ─── ViewHolder ─────────────────────────────────────────────────────────

    class HolidayViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView tvMonth;
        TextView tvDay;
        TextView tvName;
        TextView tvWeekday;
        TextView tvCountdown;
        TextView tvBadge;
        ShapeableImageView ivFavorite;

        HolidayViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.card_holiday);
            tvMonth     = itemView.findViewById(R.id.tv_month);
            tvDay       = itemView.findViewById(R.id.tv_day);
            tvName      = itemView.findViewById(R.id.tv_holiday_name);
            tvWeekday   = itemView.findViewById(R.id.tv_weekday);
            tvCountdown = itemView.findViewById(R.id.tv_countdown);
            tvBadge     = itemView.findViewById(R.id.tv_badge);
            ivFavorite  = itemView.findViewById(R.id.iv_favorite);
        }

        void bind(Holiday holiday, int pos) {
            // Date labels
            tvMonth.setText(DateUtils.getMonthAbbrev(holiday.getDate()));
            tvDay.setText(DateUtils.getDayOfMonth(holiday.getDate()));
            tvName.setText(holiday.getName());
            tvWeekday.setText(DateUtils.getWeekdayName(holiday.getDate()));
            tvCountdown.setText(DateUtils.getCountdownLabel(holiday.getDate()));

            // Badge: National vs State
            if (holiday.isNational()) {
                tvBadge.setText("National");
                tvBadge.setBackgroundResource(R.drawable.bg_badge_national);
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.on_primary));
            } else {
                List<String> states = holiday.getStates();
                String label = (states != null && !states.isEmpty())
                        ? states.size() + " Länder"
                        : "Regional";
                tvBadge.setText(label);
                tvBadge.setBackgroundResource(R.drawable.bg_badge_state);
                tvBadge.setTextColor(ContextCompat.getColor(context, R.color.on_surface));
            }

            // Favorite icon
            ivFavorite.setImageResource(holiday.isFavorite()
                    ? R.drawable.ic_favorite_filled
                    : R.drawable.ic_favorite_outline);
            ivFavorite.setColorFilter(ContextCompat.getColor(context,
                    holiday.isFavorite() ? R.color.primary : R.color.outline));

            // Today highlight
            long daysUntil = DateUtils.getDaysUntil(holiday.getDate());
            if (daysUntil == 0) {
                cardView.setStrokeColor(ContextCompat.getColor(context, R.color.tertiary_container));
                cardView.setStrokeWidth(2);
            } else {
                cardView.setStrokeWidth(0);
            }

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onHolidayClick(holiday, pos);
            });
            ivFavorite.setOnClickListener(v -> {
                if (favoriteListener != null) favoriteListener.onFavoriteClick(holiday, pos);
            });
        }
    }
}
