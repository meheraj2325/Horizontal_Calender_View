package com.example.user.horizontalcalenderview2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainActivity extends AppCompatActivity {

    TextView dateOfToday;
    RecyclerView medicineInfoRecyclerView;
    List<SingleReminderInfo> singleReminderInfoList = new ArrayList<>();
    ReminderDBHelper reminderDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderDBHelper = new ReminderDBHelper(this);
        SQLiteDatabase sqLiteDatabase = reminderDBHelper.getWritableDatabase();

        prepareRecyclerView();

        dateOfToday = findViewById(R.id.date_of_today_id);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        Calendar date = Calendar.getInstance();
        String today = DateFormat.format("EEEE, MMM d, yyyy", date).toString();
        dateOfToday.setText(today);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                try{
                    String selectedDateStr = DateFormat.format("EEEE, MMM d, yyyy", date).toString();
                    dateOfToday.setText(selectedDateStr);
                    dateOfToday.setVisibility(View.VISIBLE);

                    String givenDate = DateFormat.format("dd/MM/yyyy", date).toString();
                    retrieveReminders(givenDate);

                    Toast.makeText(getApplicationContext(), selectedDateStr + " selected!", Toast.LENGTH_LONG).show();
                    Log.i("onDateSelected", selectedDateStr + " - Position = " + position);
                }
                    catch (Exception e){
                    Log.d("hi", "onDateSelected: " + e);
                }
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onStart() {
        super.onStart();

        Calendar date = Calendar.getInstance();
        String today = DateFormat.format("dd/MM/yyyy", date).toString();
        retrieveReminders(today);
    }

    private void prepareRecyclerView() {
        medicineInfoRecyclerView = findViewById(R.id.med_info_recycler_view_id);
        medicineInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicineInfoRecyclerView.setAdapter(new SingleReminderAdapter(this,singleReminderInfoList));
    }

    private void retrieveReminders(String today)
    {
        /*singleReminderInfoList.add(new SingleReminderInfo(1,"Take","09:30 PM",
                "NAPA Tab.","1 tab per Dose"));

        singleReminderInfoList.add(new SingleReminderInfo(1,"NotNotified","10:30 AM",
                "ACE Tab.","1 tab per Dose"));

        singleReminderInfoList.add(new SingleReminderInfo(1,"NotNotified","10:30 AM",
                "ACE Tab.","1 tab per Dose"));*/

        singleReminderInfoList.clear();

        Cursor cursor = reminderDBHelper.displayAllReminders(today);

        if(cursor.getCount()==0){
            SingleReminderAdapter singleReminderAdapter = (SingleReminderAdapter) medicineInfoRecyclerView.getAdapter();
            singleReminderAdapter.setCurDate(today);
            if (singleReminderAdapter != null) {
                singleReminderAdapter.setSingleReminderInfoList(singleReminderInfoList);
                singleReminderAdapter.notifyDataSetChanged();
            }
            return;
        }
        while (cursor.moveToNext()){
            singleReminderInfoList.add(new SingleReminderInfo(
                            cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status")),
                            cursor.getString(cursor.getColumnIndexOrThrow("_med_time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("med_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("med_dose"))
                    )
            );
        }
        SingleReminderAdapter singleReminderAdapter = (SingleReminderAdapter) medicineInfoRecyclerView.getAdapter();
        singleReminderAdapter.setCurDate(today);
        if (singleReminderAdapter != null) {
            singleReminderAdapter.setSingleReminderInfoList(singleReminderInfoList);
            singleReminderAdapter.notifyDataSetChanged();
        }

    }

    public void onClickFloatingButton(View v)
    {
        Intent intent = new Intent(MainActivity.this,AddEditCreateActivity.class);
        startActivity(intent);
    }
}
