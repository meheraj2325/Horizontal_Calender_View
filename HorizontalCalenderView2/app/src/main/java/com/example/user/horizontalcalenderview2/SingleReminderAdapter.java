package com.example.user.horizontalcalenderview2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SingleReminderAdapter extends RecyclerView.Adapter<SingleReminderAdapter.SingleReminderViewHolder> {

    private Context currentContext;
    private List<SingleReminderInfo> singleReminderInfoList;

    public SingleReminderAdapter(Context currentContext, List<SingleReminderInfo> singleReminderInfoList) {
        this.currentContext = currentContext;
        this.singleReminderInfoList = singleReminderInfoList;
    }

    @NonNull
    @Override
    public SingleReminderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(currentContext);
        View view = inflater.inflate(R.layout.reminder_info_viewer_in_list, null);
        return new SingleReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleReminderViewHolder holder, int i) {
        final SingleReminderInfo singleReminder = singleReminderInfoList.get(i);

        holder.medDoseInfoView.setText(singleReminder.getReminderMedDoseInfo());
        holder.medNameView.setText(singleReminder.getReminderMedName());
        holder.medTimeView.setText(singleReminder.getReminderTime());

        holder.reminderStatusImageView.setImageResource(getLogoResource(singleReminder.getReminderStatus()));
    }

    @Override
    public int getItemCount() {
        return singleReminderInfoList.size();
    }

    private int getLogoResource(String status) {
        switch (status) {
            case "Skip": return R.drawable.ic_skip_alert_red;
            case "Snooze": return R.drawable.ic_snooze_alert_grey;
            case "Take": return R.drawable.ic_taken_green;
            case "NotNotified": return R.drawable.ic_not_reminded_grey;
            default: return R.drawable.ic_not_reminded_grey;
        }
    }

    class SingleReminderViewHolder extends RecyclerView.ViewHolder {
        TextView medDoseInfoView, medNameView, medTimeView;
        ImageView reminderStatusImageView;

        public SingleReminderViewHolder(View itemView) {
            super(itemView);

            reminderStatusImageView = itemView.findViewById(R.id.reminder_status_image_id);
            medDoseInfoView = itemView.findViewById(R.id.med_dose_info_id);
            medNameView = itemView.findViewById(R.id.med_name_id);
            medTimeView = itemView.findViewById(R.id.med_time_view_id);

        }
    }
}
