package com.example.user.horizontalcalenderview2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<SingleReminderTime> itemModelList;
    public CustomAdapter(Context context, ArrayList<SingleReminderTime> modelList) {
        this.context = context;
        this.itemModelList = modelList;
    }
    @Override
    public int getCount() {
        return itemModelList.size();
    }
    @Override
    public Object getItem(int position) {
        return itemModelList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.reminder_time_view, null);
            TextView singleTimeView =  convertView.findViewById(R.id.single_time_id);
            ImageButton editTimeButton = convertView.findViewById(R.id.edit_time);
            SingleReminderTime temp = itemModelList.get(position);
            singleTimeView.setText(temp.getTime());

            /*editTimeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemModelList.remove(position);
                    notifyDataSetChanged();
                }
            });*/
        }
        return convertView;
    }

    public void updateRemindersList(ArrayList<SingleReminderTime> newlist) {
        itemModelList = newlist;
        this.notifyDataSetChanged();
    }
}
