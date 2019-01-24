package com.example.user.horizontalcalenderview2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
                "      _id INTEGER," +
                "      _med_time TEXT," +
                "      _cur_date TEXT," +
                "      status TEXT," +
                "PRIMARY KEY (_id, _med_time,_cur_date)" +
                "  )";

        try {
            db.execSQL(createReminderTableSql);
            db.execSQL(createReminderTableSq2);

            Toast.makeText(context,"dBOnCreate is called",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(context,"Exception : "+e,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertDataToDBTable1(String medName, String medDose, String startDate, String endDate)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("med_name",medName);
        contentValues1.put("med_dose",medDose);
        contentValues1.put("start_date",startDate);
        contentValues1.put("end_date",endDate);

        long rowId = sqLiteDatabase.insert(TABLE_NAME_1,null,contentValues1);
        return rowId;
    }

    public long insertDataToDBTable2(long id,ArrayList<SingleReminderTime> timeList, String startDate, String endDate)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long rowId = 0;

        String curDate = startDate;

        for(int j=1;j<=90;j++){
            for(int i=0;i<timeList.size();i++){
                String medTime = timeList.get(i).getTime();
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_id",id);
                contentValues2.put("_med_time",medTime);
                contentValues2.put("_cur_date",curDate);
                contentValues2.put("status","NotNotified");

                rowId = sqLiteDatabase.insert(TABLE_NAME_2,null,contentValues2);
                if(rowId==-1) return rowId;
            }
            if(curDate.equals(endDate)) break;
            curDate = dateIncrementByOne(curDate);
        }
        return rowId;
    }

    public boolean leapYear(int year)
    {
        if(((year % 4 == 0) && (year % 100!= 0)) || (year%400 == 0)) return true;
        return false;
    }

    public String dateIncrementByOne(String date1)
    {
        int year1 = Integer.parseInt(date1.substring(6,10));
        int month1 = Integer.parseInt(date1.substring(3,5));
        int day1 = Integer.parseInt(date1.substring(0,2));

        if(month1==2){
            if((day1==29 && leapYear(year1)) || (day1==28 && !leapYear(year1))){
                day1 = 1;
                month1++;
                return modifiedDateFormat(year1,month1,day1);
            }
            else return modifiedDateFormat(year1,month1,day1+1);
        }
        else if(month1==12 && day1==31) return modifiedDateFormat(year1+1,01,01);
        else if((month1==1 || month1==3 || month1 == 5 || month1 == 7 || month1 ==8 || month1 == 10) && day1 ==31){
            return modifiedDateFormat(year1,month1+1,01);
        }
        else if((month1==4 || month1==6 || month1 == 9 || month1 == 11) && day1 ==30){
            return modifiedDateFormat(year1,month1+1,01);
        }
        return modifiedDateFormat(year1,month1,day1+1);
    }

    public String modifiedDateFormat(int year, int month, int dayOfMonth)
    {
        String dayString;
        if(dayOfMonth<10 && month<10) dayString = "0" + dayOfMonth + "/0" + month + "/" + year;
        else if(dayOfMonth<10) dayString = "0" + dayOfMonth + "/" + month + "/" + year;
        else if(month<10) dayString =  dayOfMonth + "/0" + month + "/" + year;
        else dayString = dayOfMonth + "/" + month + "/" + year;

        return dayString;

    }

    public Cursor displayAllReminders(String curDate)
    {
        String queryReminders = "SELECT _id,status,_med_time,med_name,med_dose " +
                "FROM " + TABLE_NAME_1 + " NATURAL JOIN " + TABLE_NAME_2 + "   " +
                "WHERE _cur_date = '" + curDate +"'";

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(queryReminders,null);
        return cursor;
    }

    public Cursor getStartAndEndDate(int id){
        String query = "SELECT start_date,end_date " +
                        "FROM" + TABLE_NAME_1 + "  " +
                        "WHERE _id = " + id ;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    public Cursor getReminderTimes(int id){
        String query = "SELECT DISTINCT _med_time" +
                "FROM" + TABLE_NAME_2 + "  " +
                "WHERE _id = " + id ;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }

    public void deleteASingleReminderHistory(int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME_1,"_id = ?",new String[] {String.valueOf(id)});
        sqLiteDatabase.delete(TABLE_NAME_2,"_id = ?",new String[] {String.valueOf(id)});
    }

    public boolean updateStatusOfAReminder(int id,String medTime,String curDate,String status){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("_id",id);
        contentValues2.put("_med_time",medTime);
        contentValues2.put("_cur_date",curDate);
        contentValues2.put("status",status);

        sqLiteDatabase.update(TABLE_NAME_2,contentValues2,"_id = ? AND _med_time = ? AND _cur_date = ?",new String[]{String.valueOf(id),medTime,curDate});
        return true;
    }
}
