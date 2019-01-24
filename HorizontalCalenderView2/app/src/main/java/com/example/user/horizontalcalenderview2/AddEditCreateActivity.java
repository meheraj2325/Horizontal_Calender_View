package com.example.user.horizontalcalenderview2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class AddEditCreateActivity extends AppCompatActivity {

    String[] doseQuantities;
    private SingleReminderInfo singleReminderInfo;
    private EditText medName,customDose;
    private TextView medInfo,doseInfo,singleTime,selectTime,startDate,endDate;
    private Spinner doseQuantitiesSpinner;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private int currentHour,currentMin,currentYear,currentMonth,currentDay;
    private ListView reminderTimesListView;
    private AlertDialog.Builder alertDialogBuilder;
    ArrayList<SingleReminderTime> reminderTimesArray;
    CustomAdapter reminderTimesAdapter;

    ReminderDBHelper reminderDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_create);

        reminderDBHelper = new ReminderDBHelper(this);
        SQLiteDatabase sqLiteDatabase = reminderDBHelper.getWritableDatabase();

        bindWidgets();

        if (getIntent().getExtras() != null) {
            Object singleReminderInfo = getIntent().getExtras().get("reminder");
            if (singleReminderInfo != null) {
                this.singleReminderInfo = (SingleReminderInfo) singleReminderInfo;
            }
        }

        bindValuesToInputs();
        setTitle(singleReminderInfo == null ? R.string.add_reminder : R.string.edit_reminder);

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<String>(this,R.layout.dose_quantity_view,R.id.single_quantity_id, doseQuantities);
        doseQuantitiesSpinner.setAdapter(quantityAdapter);

        reminderTimesArray = new ArrayList<>();
        reminderTimesAdapter = new CustomAdapter(getApplicationContext(),reminderTimesArray);
        ///reminderTimesListView.setEmptyView(findViewById(R.id.empty));
        reminderTimesListView.setAdapter(reminderTimesAdapter);

        reminderTimesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimePicker timePicker = new TimePicker(AddEditCreateActivity.this);

                currentHour = timePicker.getCurrentHour();
                currentMin = timePicker.getCurrentMinute();

                timePickerDialog = new TimePickerDialog(AddEditCreateActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                singleTime = findViewById(R.id.single_time_id);

                                String status = "AM",timeString;
                                if(hourOfDay > 11) status = "PM";
                                int hourOf12HourFormat;
                                if(hourOfDay > 12) hourOf12HourFormat = hourOfDay - 12;
                                else hourOf12HourFormat = hourOfDay;
                                if(hourOfDay==0) hourOf12HourFormat = 12;

                                if(hourOf12HourFormat<10 && minute<10) timeString = "0" + hourOf12HourFormat + ":0" + minute + " " + status;
                                else if(hourOf12HourFormat<10) timeString = "0" + hourOf12HourFormat + ":" + minute + " " + status;
                                else if(minute<10) timeString = hourOf12HourFormat + ":0" + minute + " " + status;
                                else timeString = hourOf12HourFormat + ":" + minute + " " + status;
                                singleTime.setText(timeString);
                            }
                        },currentHour,currentMin,false);

                timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Your code
                    }
                });

                timePickerDialog.show();
            }
        });

        initializeStartDate();
    }

    private void initializeStartDate()
    {
        final DatePicker datePicker = new DatePicker(this);
        currentDay = datePicker.getDayOfMonth();
        currentMonth = (datePicker.getMonth())+1;
        currentYear = datePicker.getYear();

        String dayString;
        if(currentDay<10 && currentMonth<10) dayString = "0" + currentDay + "/0" + currentMonth + "/" + currentYear;
        else if(currentDay<10) dayString = "0" + currentDay + "/" + currentMonth + "/" + currentYear;
        else if(currentMonth<10) dayString =  currentDay + "/0" + currentMonth + "/" + currentYear;
        else dayString = currentDay + "/" + currentMonth + "/" + currentYear;

        startDate.setText(dayString);
    }

    public void bindWidgets()
    {
        doseQuantities = getResources().getStringArray(R.array.dose_quantities);
        medName = findViewById(R.id.med_name_id);
        customDose = findViewById(R.id.custom_dose_id);
        medInfo = findViewById(R.id.med_info_id);
        doseInfo = findViewById(R.id.dose_info_id);
        doseQuantitiesSpinner = findViewById(R.id.dose_quantities_id);
        reminderTimesListView = findViewById(R.id.reminder_times_list_id);
        startDate = findViewById(R.id.start_date_id);
        endDate = findViewById(R.id.end_date_id);
    }

    public void bindValuesToInputs()
    {
        if(singleReminderInfo!=null){
            int iid = singleReminderInfo.getId();

            medName.setText(singleReminderInfo.getReminderMedName());
            String dose = singleReminderInfo.getReminderMedDoseInfo();
            String quantity = findDosageQuantity(dose);
            if(!quantity.equals("none")){
                int idx = dose.lastIndexOf(quantity);
                dose = dose.substring(0,idx);
            }
            customDose.setText(dose);
            //doseQuantitiesSpinner.setSelection(((ArrayAdapter<String>) doseQuantitiesSpinner.getAdapter()).getPosition(quantity));

            /*Cursor cursor1 = reminderDBHelper.getStartAndEndDate(iid);
            Cursor cursor2 = reminderDBHelper.getReminderTimes(iid);

            String stDate,edDate;
            while(cursor1.moveToNext()){
                stDate = cursor1.getString(cursor1.getColumnIndexOrThrow("start_date"));
                edDate = cursor1.getString(cursor1.getColumnIndexOrThrow("end_date"));
                startDate.setText(stDate);
                endDate.setText(edDate);
            }

            while (cursor2.moveToNext()){
                String time = cursor2.getString(cursor2.getColumnIndexOrThrow("_med_time"));
                SingleReminderTime temp = new SingleReminderTime(time);
                reminderTimesArray.add(temp);
                reminderTimesAdapter.notifyDataSetChanged();
                reminderTimesAdapter.updateRemindersList(reminderTimesArray);
            }
            selectTime.setText(R.string.time_add);*/
        }
    }

    public String findDosageQuantity(String s){
        if(s.lastIndexOf("ml")!=-1) return "ml";
        if(s.lastIndexOf("mg")!=-1) return "mg";
        if(s.lastIndexOf("cc")!=-1) return "cc";
        if(s.lastIndexOf("gr")!=-1) return "gr";
        if(s.lastIndexOf("tbsp")!=-1) return "tbsp";
        if(s.lastIndexOf("tsp")!=-1) return "tsp";
        if(s.lastIndexOf("squeezes")!=-1) return "squeezes";
        if(s.lastIndexOf("drops")!=-1) return "drops";
        return "none";
    }

    private boolean validateInput() {
        boolean allInputsValid = true;

        for(EditText input
                : new EditText[]{medName,customDose}) {
            if (input.getText().toString().isEmpty()) {
                showError(input, R.string.required);
                allInputsValid = false;
            }
        }
        if(reminderTimesArray.isEmpty()){
            try{
                Toast.makeText(this,"Please select a time",Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this,"Exception " + e ,Toast.LENGTH_SHORT).show();;
            }
            allInputsValid = false;
        }

        return allInputsValid;
    }

    private void showError(EditText field, int messageRes) {
        field.setError(getString(messageRes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_edit_create,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.cancel_action){
            alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage(R.string.canceling_reminder_alert_message);
            alertDialogBuilder.setTitle(R.string.canceling_reminder_alert_tile);
            alertDialogBuilder.setIcon(R.drawable.ic_alert);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(AddEditCreateActivity.this,MainActivity.class);
                    startActivity(intent);
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

            return true;
        }
        if(item.getItemId()==R.id.done_reminder){
            boolean flag = onSave();
            if(flag){
                Intent intent = new Intent(AddEditCreateActivity.this,MainActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onSave()
    {
        if(singleReminderInfo!=null){
            int iid = singleReminderInfo.getId();
            reminderDBHelper.deleteASingleReminderHistory(iid);
        }

        if(validateInput()){
            String medNameString = medName.getText().toString();
            String customDoseString = customDose.getText().toString();
            String temp = doseQuantitiesSpinner.getSelectedItem().toString();
            if(!temp.equals("none"))customDoseString += (" " + temp);

            String startDateOfReminder = startDate.getText().toString();
            String endDateOfReminder = endDate.getText().toString();

            Log.d("HI",medNameString + " " + customDoseString + " " + startDateOfReminder + " " + endDateOfReminder);

            long rowId = reminderDBHelper.insertDataToDBTable1(medNameString,customDoseString,startDateOfReminder,endDateOfReminder);
            if(rowId==-1){
                try
                {
                    Toast.makeText(getApplicationContext(),"Unsuccesfull" ,Toast.LENGTH_LONG).show();
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Exception " + e,Toast.LENGTH_LONG).show();
                }
            }
            else{
                try
                {
                    Toast.makeText(getApplicationContext(),"Row is successfully inserted in table 1",Toast.LENGTH_SHORT).show();

                    long rowId2 = reminderDBHelper.insertDataToDBTable2(rowId,reminderTimesArray,startDateOfReminder,endDateOfReminder);

                    if(rowId2==-1){
                        try
                        {
                            Toast.makeText(getApplicationContext(),"Unsuccesfull" ,Toast.LENGTH_LONG).show();
                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Exception " + e,Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        try
                        {
                            Toast.makeText(getApplicationContext(),"Row is successfully inserted in table 2",Toast.LENGTH_SHORT).show();
                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Exception " + e,Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Exception " + e,Toast.LENGTH_LONG).show();
                }
            }

            return true;
        }
        return false;
    }

    public void reminderTimePicker(View v)
    {
        final TimePicker timePicker = new TimePicker(this);

        currentHour = timePicker.getCurrentHour();
        currentMin = timePicker.getCurrentMinute();

        timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectTime = findViewById(R.id.select_time_id);
                        singleTime = findViewById(R.id.single_time_id);

                        String status = "AM",timeString;
                        if(hourOfDay > 11) status = "PM";
                        int hourOf12HourFormat;
                        if(hourOfDay > 12) hourOf12HourFormat = hourOfDay - 12;
                        else hourOf12HourFormat = hourOfDay;
                        if(hourOfDay==0) hourOf12HourFormat = 12;

                        if(hourOf12HourFormat<10 && minute<10) timeString = "0" + hourOf12HourFormat + ":0" + minute + " " + status;
                        else if(hourOf12HourFormat<10) timeString = "0" + hourOf12HourFormat + ":" + minute + " " + status;
                        else if(minute<10) timeString = hourOf12HourFormat + ":0" + minute + " " + status;
                        else timeString = hourOf12HourFormat + ":" + minute + " " + status;

                        SingleReminderTime temp = new SingleReminderTime(timeString);
                        reminderTimesArray.add(temp);
                        reminderTimesAdapter.notifyDataSetChanged();
                        Log.d("HI", "ArrayList size : " + reminderTimesArray.size());
                        reminderTimesAdapter.updateRemindersList(reminderTimesArray);
                        selectTime.setText(R.string.time_add);

                    }
                },currentHour,currentMin,false);


        timePickerDialog.show();
    }

    public void reminderStartDatePicker(View v) {

        final DatePicker datePicker = new DatePicker(this);
        currentDay = datePicker.getDayOfMonth();
        currentMonth = datePicker.getMonth();
        currentYear = datePicker.getYear();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                startDate = findViewById(R.id.start_date_id);
                String dayString;
                if(dayOfMonth<10 && month<10) dayString = "0" + dayOfMonth + "/0" + month + "/" + year;
                else if(dayOfMonth<10) dayString = "0" + dayOfMonth + "/" + month + "/" + year;
                else if(month<10) dayString =  dayOfMonth + "/0" + month + "/" + year;
                else dayString = dayOfMonth + "/" + month + "/" + year;

                startDate.setText(dayString);
            }
        },currentYear,currentMonth,currentDay);

        datePickerDialog.show();
    }

    public void reminderEndDatePicker(View v) {

        final DatePicker datePicker = new DatePicker(this);
        currentDay = datePicker.getDayOfMonth();
        currentMonth = datePicker.getMonth();
        currentYear = datePicker.getYear();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                endDate = findViewById(R.id.end_date_id);
                String dayString;
                if(dayOfMonth<10 && month<10) dayString = "0" + dayOfMonth + "/0" + month + "/" + year;
                else if(dayOfMonth<10) dayString = "0" + dayOfMonth + "/" + month + "/" + year;
                else if(month<10) dayString =  dayOfMonth + "/0" + month + "/" + year;
                else dayString = dayOfMonth + "/" + month + "/" + year;

                endDate.setText(dayString);
            }
        },currentYear,currentMonth,currentDay);

        datePickerDialog.show();
    }
}
