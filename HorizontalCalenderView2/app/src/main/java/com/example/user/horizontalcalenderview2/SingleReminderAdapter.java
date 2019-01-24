package com.example.user.horizontalcalenderview2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

public class SingleReminderAdapter extends RecyclerView.Adapter<SingleReminderAdapter.SingleReminderViewHolder> {

    private Context currentContext;
    private List<SingleReminderInfo> singleReminderInfoList;
    private String curDate;

    public SingleReminderAdapter(Context currentContext, List<SingleReminderInfo> singleReminderInfoList) {
        this.currentContext = currentContext;
        this.singleReminderInfoList = singleReminderInfoList;
    }

    public void setSingleReminderInfoList(List<SingleReminderInfo> singleReminderInfoList) {
        this.singleReminderInfoList = singleReminderInfoList;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
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

        final int reminderId = singleReminder.getId();
        final String reminderMedTime = singleReminder.getReminderTime();

        holder.medDoseInfoView.setText(singleReminder.getReminderMedDoseInfo());
        holder.medNameView.setText(singleReminder.getReminderMedName());
        holder.medTimeView.setText(singleReminder.getReminderTime());

        holder.reminderStatusImageView.setImageResource(getLogoResource(singleReminder.getReminderStatus()));

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(currentContext);
                customDialog.setContentView(R.layout.remainder_details_in_dialog);
                customDialog.setTitle("Title...");

                ImageButton deleteReminderButton = customDialog.findViewById(R.id.delete_reminder_button_id);
                ImageButton editReminderButton = customDialog.findViewById(R.id.edit_reminder_button_id);

                TextView medicineNameView = customDialog.findViewById(R.id.medicine_name_view_id);
                TextView statusView = customDialog.findViewById(R.id.status_view_id);
                TextView dosageDescriptionView = customDialog.findViewById(R.id.dosage_description_view_id);
                TextView scheduleView = customDialog.findViewById(R.id.schedule_view_id);
                TextView skipView = customDialog.findViewById(R.id.skip_view_id);
                TextView snoozeView = customDialog.findViewById(R.id.snooze_view_id);
                TextView takeView = customDialog.findViewById(R.id.take_view_id);

                FloatingActionButton skipButton = customDialog.findViewById(R.id.skip_button_id);
                FloatingActionButton snoozeButton = customDialog.findViewById(R.id.snooze_button_id);
                FloatingActionButton takeButton = customDialog.findViewById(R.id.take_button_id);

                medicineNameView.setText(singleReminder.getReminderMedName());
                String status = singleReminder.getReminderStatus();
                if(status.equals("Take")) status = "Taken";
                else if(status.equals("Skip")) status = "Skipped";
                else if(status.equals("Snooze")) status = "Snoozed";
                else status = "";

                statusView.setText(status);
                dosageDescriptionView.setText(singleReminder.getReminderMedDoseInfo());

                String schedule = "Scheduled for " + singleReminder.getReminderTime() + ", " + curDate;
                scheduleView.setText(schedule);

                deleteReminderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentContext);

                        alertDialogBuilder.setMessage(R.string.deleting_reminder_alert_message);
                        alertDialogBuilder.setTitle(R.string.deleting_reminder_alert_tile);
                        alertDialogBuilder.setIcon(R.drawable.ic_alert);
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                ReminderDBHelper reminderDBHelper = new ReminderDBHelper(currentContext);
                                reminderDBHelper.deleteASingleReminderHistory(reminderId);

                                ListIterator<SingleReminderInfo> listIterator = singleReminderInfoList.listIterator();
                                while(listIterator.hasNext()){
                                    int iid = listIterator.next().getId();
                                    if(iid==reminderId) {
                                        listIterator.remove();
                                        notifyDataSetChanged();
                                    }
                                }

                                customDialog.dismiss();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("CANCEL",new  DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });

                editReminderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(currentContext, AddEditCreateActivity.class).putExtra("reminder", singleReminder);
                        currentContext.startActivity(intent);
                    }
                });

                skipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(currentContext);
                        reminderDBHelper.updateStatusOfAReminder(reminderId,reminderMedTime,curDate,"Skip");

                        /*ListIterator<SingleReminderInfo> listIterator = singleReminderInfoList.listIterator();
                        while(listIterator.hasNext()){
                            int iid = listIterator.next().getId();
                            String time = listIterator.next().getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                listIterator.next().setReminderStatus("Skip");
                                notifyDataSetChanged();
                            }
                        }*/

                        for(int i=0;i<singleReminderInfoList.size();i++){
                            int iid = singleReminderInfoList.get(i).getId();
                            String time = singleReminderInfoList.get(i).getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                singleReminderInfoList.get(i).setReminderStatus("Skip");
                                notifyDataSetChanged();
                                break;
                            }
                        }

                        customDialog.dismiss();
                    }
                });

                snoozeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(currentContext);
                        reminderDBHelper.updateStatusOfAReminder(reminderId,reminderMedTime,curDate,"Snooze");

                        /*ListIterator<SingleReminderInfo> listIterator = singleReminderInfoList.listIterator();
                        while(listIterator.hasNext()){
                            int iid = listIterator.next().getId();
                            String time = listIterator.next().getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                listIterator.next().setReminderStatus("Snooze");
                                notifyDataSetChanged();
                            }
                        }*/

                        for(int i=0;i<singleReminderInfoList.size();i++){
                            int iid = singleReminderInfoList.get(i).getId();
                            String time = singleReminderInfoList.get(i).getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                singleReminderInfoList.get(i).setReminderStatus("Snooze");
                                notifyDataSetChanged();
                                break;
                            }
                        }

                        customDialog.dismiss();
                    }
                });

                takeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReminderDBHelper reminderDBHelper = new ReminderDBHelper(currentContext);
                        reminderDBHelper.updateStatusOfAReminder(reminderId,reminderMedTime,curDate,"Take");

                        /*ListIterator<SingleReminderInfo> listIterator = singleReminderInfoList.listIterator();
                        while(listIterator.hasNext()){
                            int iid = listIterator.next().getId();
                            String time = listIterator.next().getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                listIterator.next().setReminderStatus("Take");
                                notifyDataSetChanged();
                            }
                        }*/

                        for(int i=0;i<singleReminderInfoList.size();i++){
                            int iid = singleReminderInfoList.get(i).getId();
                            String time = singleReminderInfoList.get(i).getReminderTime();

                            if(iid==reminderId && time.equals(reminderMedTime)) {
                                singleReminderInfoList.get(i).setReminderStatus("Take");
                                notifyDataSetChanged();
                                break;
                            }
                        }

                        customDialog.dismiss();
                    }
                });

                customDialog.show();
            }
        });
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
