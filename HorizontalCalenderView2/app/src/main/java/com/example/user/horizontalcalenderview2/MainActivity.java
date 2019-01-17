package com.example.user.horizontalcalenderview2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainActivity extends AppCompatActivity {

    TextView dateOfToday;
    RecyclerView medicineInfoRecyclerView;
    List<SingleReminderInfo> singleReminderInfoList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareListView();

        dateOfToday = findViewById(R.id.date_of_today_id);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                dateOfToday.setText("Hi i m there");
                dateOfToday.setVisibility(View.VISIBLE);
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

        retrieveCards();
    }

    private void prepareListView() {
        medicineInfoRecyclerView = findViewById(R.id.med_info_recycler_view_id);
        medicineInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicineInfoRecyclerView.setAdapter(new SingleReminderAdapter(this,singleReminderInfoList));
    }

    private void retrieveCards()
    {
        singleReminderInfoList.add(new SingleReminderInfo(1,"Take","09:30 PM",
                "NAPA Tab.","1 tab per Dose"));

        singleReminderInfoList.add(new SingleReminderInfo(1,"NotNotified","10:30 AM",
                "ACE Tab.","1 tab per Dose"));


        singleReminderInfoList.add(new SingleReminderInfo(1,"NotNotified","10:30 AM",
                "ACE Tab.","1 tab per Dose"));

    }

    public void onClickFloatingButton(View v)
    {
        Intent intent = new Intent(MainActivity.this,AddEditCreateActivity.class);
        startActivity(intent);
    }
}
