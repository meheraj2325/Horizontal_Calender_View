package com.example.user.horizontalcalenderview2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class ReminderDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Reminder.db";
    private static final String TABLE_NAME_1 = "reminder_med_details";
    private static final String TABLE_NAME_2 = "reminder_date_time_details";
    private static final int DB_VERSION= 1;
    private Context context;

    public ReminderDBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createReminderTableSql
                = "CREATE TABLE "+TABLE_NAME_1+" (" +
                "      _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "      med_name TEXT," +
                "      med_dose TEXT," +
                "      start_date TEXT," +
                "      end_date TEXT" +
                "  )";

        String createReminderTableSq2
                = "CREATE TABLE "+TABLE_NAME_2+" (" +
                "      _id INTEGER PRIMARY KEY," +
                "      med_time TEXT PRIMARY KEY," +
                "      cur_date TEXT PRIMARY KEY" +
                "  )";

        try {
            Toast.makeText(context,"dBOnCreate is called",Toast.LENGTH_SHORT).show();

            db.execSQL(createReminderTableSql);
            db.execSQL(createReminderTableSq2);
        }catch (Exception e)
        {
            Toast.makeText(context,"Exception : "+e,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertDataToDB(String medName, String medDose, String startDate, String endDate, ArrayList<SingleReminderTime> timeList)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    }
}
