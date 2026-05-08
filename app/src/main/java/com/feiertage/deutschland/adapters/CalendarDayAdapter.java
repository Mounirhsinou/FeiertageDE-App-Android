package com.feiertage.deutschland.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.feiertage.deutschland.R;
import com.feiertage.deutschland.models.Holiday;
import com.feiertage.deutschland.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CalendarDayAdapter – powers the month-grid calendar view.
 * Each cell is a day number; cells with holidays get a colored dot.
 */
public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(int year, int month, int day, List<Holiday> holidays);
    }

    // Represents one grid cell (may be empty padding, weekend, or holiday day)
    public static class DayCell {
        public int day;            // 0 = empty padding
        public int year;
        public int month;
        public boolean isToday;
        public boolean isWeekend;
        public List<Holiday> holidays = new ArrayList<>();
    }

    private final Context context;
    private final List<DayCell> cells;
    private OnDayClickListener clickListener;

    public CalendarDayAdapter(Context context) {
        this.context = context;
        this.cells   = new ArrayList<>();
    }

    private int resolveColor(@AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    public void setCells(List<DayCell> cells) {
        this.cells.clear();
        if (cells != null) this.cells.addAll(cells);
        notifyDataSetChanged();
    }

    public void setOnDayClickListener(OnDayClickListener l) {
        this.clickListener = l;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(cells.get(position));
    }

    @Override
    public int getItemCount() { return cells.size(); }

    class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View dotHoliday;
        View circleToday;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay       = itemView.findViewById(R.id.tv_cal_day);
            dotHoliday  = itemView.findViewById(R.id.dot_holiday);
            circleToday = itemView.findViewById(R.id.circle_today);
        }

        void bind(DayCell cell) {
            if (cell.day == 0) {
                // Empty padding cell
                tvDay.setText("");
                dotHoliday.setVisibility(View.INVISIBLE);
                circleToday.setVisibility(View.INVISIBLE);
                itemView.setOnClickListener(null);
                return;
            }

            tvDay.setText(String.valueOf(cell.day));

            // Today highlight
            if (cell.isToday) {
                circleToday.setVisibility(View.VISIBLE);
                tvDay.setTextColor(resolveColor(com.google.android.material.R.attr.colorOnPrimary));
            } else {
                circleToday.setVisibility(View.INVISIBLE);
                if (cell.isWeekend) {
                    tvDay.setTextColor(resolveColor(com.google.android.material.R.attr.colorOutline));
                } else {
                    tvDay.setTextColor(resolveColor(com.google.android.material.R.attr.colorOnSurface));
                }
            }

            // Holiday dot
            if (!cell.holidays.isEmpty()) {
                dotHoliday.setVisibility(View.VISIBLE);
                // National = red dot, regional = gold dot
                boolean hasNational = false;
                for (Holiday h : cell.holidays) {
                    if (h.isNational()) { hasNational = true; break; }
                }
                dotHoliday.setBackgroundResource(hasNational
                        ? R.drawable.dot_national
                        : R.drawable.dot_regional);
            } else {
                dotHoliday.setVisibility(View.INVISIBLE);
            }

            // Click
            itemView.setOnClickListener(v -> {
                if (clickListener != null && !cell.holidays.isEmpty()) {
                    clickListener.onDayClick(cell.year, cell.month, cell.day, cell.holidays);
                }
            });
        }
    }
}
